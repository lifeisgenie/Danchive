package DumbAndDumber.Danchive.api.service;

import DumbAndDumber.Danchive.api.dto.form.ExhibitRankingResponse;
import DumbAndDumber.Danchive.api.dto.form.PopularVoteRequest;
import DumbAndDumber.Danchive.api.dto.form.ProfessorEvaluationRequest;
import DumbAndDumber.Danchive.api.entity.Exhibit;
import DumbAndDumber.Danchive.api.entity.ExhibitAward;
import DumbAndDumber.Danchive.api.entity.PopularVote;
import DumbAndDumber.Danchive.api.entity.PopularVoterType;
import DumbAndDumber.Danchive.api.entity.ProfessorEvaluation;
import DumbAndDumber.Danchive.api.entity.User;
import DumbAndDumber.Danchive.api.repository.ExhibitRepository;
import DumbAndDumber.Danchive.api.repository.PopularVoteRepository;
import DumbAndDumber.Danchive.api.repository.ProfessorEvaluationRepository;
import DumbAndDumber.Danchive.api.util.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class VoteService {

    private final ExhibitRepository exhibitRepository;
    private final PopularVoteRepository popularVoteRepository;
    private final ProfessorEvaluationRepository professorEvaluationRepository;

    /**
     * 인기상 투표 (방문객 + 참여 팀)
     */
    public void submitPopularVote(String term, PopularVoteRequest request) {
        Exhibit exhibit = exhibitRepository.findById(request.getExhibitId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 작품입니다."));

        if (!Objects.equals(exhibit.getTerm(), term)) {
            throw new IllegalArgumentException("해당 학기의 작품이 아닙니다.");
        }

        // 로그인 사용자 가져오기 (없으면 방문객)
        User currentUser = getCurrentUserOrNull();
        PopularVoterType voterType;
        User voter = null;

        if (currentUser != null && "team".equals(currentUser.getRole())) {
            voter = currentUser;
            voterType = PopularVoterType.TEAM;

            // 한 학기당 한 번만 인기상 투표 가능
            if (popularVoteRepository.existsByTermAndVoter(term, voter)) {
                throw new IllegalArgumentException("이미 이번 학기에 인기상 투표를 완료했습니다.");
            }
        } else {
            voterType = PopularVoterType.VISITOR;
        }

        validateScore(request.getCreativityScore());
        validateScore(request.getCompletionScore());

        PopularVote vote = PopularVote.builder()
                .term(term)
                .exhibit(exhibit)
                .voter(voter)
                .voterType(voterType)
                .favoriteTeamName(request.getFavoriteTeamName())
                .creativityScore(request.getCreativityScore())
                .completionScore(request.getCompletionScore())
                .voterName(request.getVoterName())
                .voterContact(request.getVoterContact())
                .build();

        popularVoteRepository.save(vote);
    }

    /**
     * 교수 평가 등록/수정
     */
    public void submitProfessorEvaluation(ProfessorEvaluationRequest request) {
        User professor = SecurityUtil.getCurrentUserOrThrow();
        if (!"prof".equals(professor.getRole())) {
            throw new IllegalArgumentException("교수 권한이 필요합니다.");
        }

        Exhibit exhibit = exhibitRepository.findById(request.getExhibitId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 작품입니다."));

        validateScore(request.getTechnicalScore());
        validateScore(request.getImpactScore());
        validateScore(request.getCreativityScore());

        ProfessorEvaluation evaluation = professorEvaluationRepository
                .findByExhibit_IdAndProfessor_Id(exhibit.getId(), professor.getId())
                .orElseGet(() -> ProfessorEvaluation.builder()
                        .exhibit(exhibit)
                        .professor(professor)
                        .build()
                );

        evaluation.setTechnicalScore(request.getTechnicalScore());
        evaluation.setImpactScore(request.getImpactScore());
        evaluation.setCreativityScore(request.getCreativityScore());
        evaluation.setComment(request.getComment());

        professorEvaluationRepository.save(evaluation);
    }

    /**
     * 학기별 상 계산 + Exhibit.awards 업데이트
     * - 대상 1팀, 최우수상 2팀, 우수상 3팀 (=6팀)
     * - 인기상 1팀
     * - 총 7팀 (중복 수상은 피하도록 인기상 수상작은 교수상 대상에서 제외)
     */
    public List<ExhibitRankingResponse> calculateAndApplyAwards(String term) {
        List<Exhibit> exhibits = exhibitRepository.findByTermOrderByCreatedAtAsc(term);

        Map<Long, PopularSummary> popularMap = loadPopularSummary(term);
        Map<Long, Double> professorMap = loadProfessorSummary(term);

        // 1) 모든 작품의 awards 초기화
        exhibits.forEach(e -> e.getAwards().clear());

        // 2) 인기상 선정
        Optional<Exhibit> popularWinnerOpt = exhibits.stream()
                .max(Comparator.<Exhibit>comparingDouble(
                                e -> popularMap.getOrDefault(e.getId(), PopularSummary.EMPTY).avgScore)
                        .thenComparingLong(
                                e -> popularMap.getOrDefault(e.getId(), PopularSummary.EMPTY).count
                        ));

        Set<Long> awardedIds = new HashSet<>();

        popularWinnerOpt.ifPresent(winner -> {
            if (popularMap.getOrDefault(winner.getId(), PopularSummary.EMPTY).count > 0) {
                winner.getAwards().add(ExhibitAward.인기상);
                awardedIds.add(winner.getId());
            }
        });

        // 3) 교수 평가 기반 상 (인기상 수상작 제외)
        List<Exhibit> professorCandidates = exhibits.stream()
                .filter(e -> !awardedIds.contains(e.getId()))
                .sorted(Comparator.<Exhibit>comparingDouble(
                                e -> professorMap.getOrDefault(e.getId(), 0.0))
                        .reversed())
                .collect(Collectors.toList());

        for (int i = 0; i < professorCandidates.size(); i++) {
            Exhibit e = professorCandidates.get(i);
            if (i == 0) {
                e.getAwards().add(ExhibitAward.대상);
            } else if (i == 1 || i == 2) {
                e.getAwards().add(ExhibitAward.최우수상);
            } else if (i >= 3 && i <= 5) {
                e.getAwards().add(ExhibitAward.우수상);
            }
        }

        // 4) 저장
        exhibitRepository.saveAll(exhibits);

        // 5) 응답 생성 (수상작 우선 정렬)
        return toRankingResponses(exhibits, popularMap, professorMap);
    }

    /**
     * 학기별 랭킹 조회 (현재 awards 기준, 실시간 반영)
     */
    public List<ExhibitRankingResponse> getTermRanking(String term) {
        List<Exhibit> exhibits = exhibitRepository.findByTermOrderByCreatedAtAsc(term);
        Map<Long, PopularSummary> popularMap = loadPopularSummary(term);
        Map<Long, Double> professorMap = loadProfessorSummary(term);
        return toRankingResponses(exhibits, popularMap, professorMap);
    }

    /**
     * Admin 이 특정 인기상 투표 삭제 (방문객/팀원 투표 정리용)
     */
    public void deletePopularVote(Long voteId) {
        User admin = SecurityUtil.getCurrentUserOrThrow();
        if (!"admin".equals(admin.getRole())) {
            throw new IllegalArgumentException("관리자만 삭제할 수 있습니다.");
        }
        popularVoteRepository.deleteById(voteId);
    }

    /**
     * Admin 이 특정 교수 평가 삭제
     */
    public void deleteProfessorEvaluation(Long evalId) {
        User admin = SecurityUtil.getCurrentUserOrThrow();
        if (!"admin".equals(admin.getRole())) {
            throw new IllegalArgumentException("관리자만 삭제할 수 있습니다.");
        }
        professorEvaluationRepository.deleteById(evalId);
    }

    // ===================== 내부 유틸 =====================

    private void validateScore(int score) {
        if (score < 1 || score > 5) {
            throw new IllegalArgumentException("별점은 1~5 사이의 값만 허용됩니다.");
        }
    }

    private Map<Long, PopularSummary> loadPopularSummary(String term) {
        Map<Long, PopularSummary> map = new HashMap<>();
        for (Object[] row : popularVoteRepository.findSummaryByTerm(term)) {
            Long exhibitId = (Long) row[0];
            Double avg = (Double) row[1];
            Long count = (Long) row[2];
            map.put(exhibitId, new PopularSummary(count, avg != null ? avg : 0.0));
        }
        return map;
    }

    private Map<Long, Double> loadProfessorSummary(String term) {
        Map<Long, Double> map = new HashMap<>();
        for (Object[] row : professorEvaluationRepository.findSummaryByTerm(term)) {
            Long exhibitId = (Long) row[0];
            Double avg = (Double) row[1];
            map.put(exhibitId, avg != null ? avg : 0.0);
        }
        return map;
    }

    private List<ExhibitRankingResponse> toRankingResponses(
            List<Exhibit> exhibits,
            Map<Long, PopularSummary> popularMap,
            Map<Long, Double> professorMap
    ) {
        return exhibits.stream()
                .map(e -> {
                    PopularSummary p = popularMap.getOrDefault(e.getId(), PopularSummary.EMPTY);
                    double profScore = professorMap.getOrDefault(e.getId(), 0.0);

                    return ExhibitRankingResponse.builder()
                            .id(e.getId())
                            .title(e.getTitle())
                            .teamName(e.getTeam().getName())
                            .term(e.getTerm())
                            .categories(e.getCategories())
                            .awards(e.getAwards())
                            .avgProfessorScore(profScore)
                            .avgPopularScore(p.avgScore)
                            .popularVoteCount(p.count)
                            .createdAt(e.getCreatedAt())
                            .build();
                })
                // 상 받은 작품 우선 정렬: 대상 → 최우수상 → 우수상 → 인기상 → 미수상
                .sorted(Comparator
                        .<ExhibitRankingResponse>comparingInt(this::awardPriority)
                        .thenComparing(ExhibitRankingResponse::getCreatedAt))
                .collect(Collectors.toList());
    }

    private int awardPriority(ExhibitRankingResponse r) {
        Set<ExhibitAward> awards = r.getAwards();
        if (awards.contains(ExhibitAward.대상)) return 0;
        if (awards.contains(ExhibitAward.최우수상)) return 1;
        if (awards.contains(ExhibitAward.우수상)) return 2;
        if (awards.contains(ExhibitAward.인기상)) return 3;
        return 4; // 수상 없음
    }

    /**
     * 인증이 없으면 null 을 돌려주는 helper
     * (인기상 투표에서 방문객 구분용)
     */
    private User getCurrentUserOrNull() {
        try {
            return SecurityUtil.getCurrentUserOrThrow();
        } catch (RuntimeException e) {
            // IllegalStateException, NoSuchElementException 등
            return null;
        }
    }

    private record PopularSummary(long count, double avgScore) {
        static final PopularSummary EMPTY = new PopularSummary(0L, 0.0);
    }
}