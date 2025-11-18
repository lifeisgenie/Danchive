package DumbAndDumber.Danchive.api.controller;

import DumbAndDumber.Danchive.api.dto.ApiResponse;
import DumbAndDumber.Danchive.api.dto.form.ExhibitRankingResponse;
import DumbAndDumber.Danchive.api.dto.form.PopularVoteRequest;
import DumbAndDumber.Danchive.api.dto.form.ProfessorEvaluationRequest;
import DumbAndDumber.Danchive.api.service.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/votes")
public class VoteController {

    private final VoteService voteService;

    /**
     * 인기상 투표 (방문객 + 참여 팀)
     * term 예: "2025-2"
     */
    @PostMapping("/popular")
    public ResponseEntity<ApiResponse<Void>> submitPopularVote(
            @RequestParam("term") String term,
            @Valid @RequestBody PopularVoteRequest request
    ) {
        voteService.submitPopularVote(term, request);
        return ResponseEntity.ok(ApiResponse.success("인기상 투표가 완료되었습니다.", null));
    }

    /**
     * 교수 평가 등록/수정
     * (내부에서 role = "prof" 체크)
     */
    @PostMapping("/professor")
    public ResponseEntity<ApiResponse<Void>> submitProfessorEvaluation(
            @Valid @RequestBody ProfessorEvaluationRequest request
    ) {
        voteService.submitProfessorEvaluation(request);
        return ResponseEntity.ok(ApiResponse.success("교수 평가가 저장되었습니다.", null));
    }

    /**
     * 학기별 작품 랭킹 조회
     * - 상 받은 작품 7개(대상/최우수상/우수상/인기상) 상단 고정
     * - 아래에는 createdAt 기준 정렬된 전체 작품 (수상작 포함)
     */
    @GetMapping("/term/{term}/ranking")
    public ResponseEntity<ApiResponse<List<ExhibitRankingResponse>>> getTermRanking(
            @PathVariable String term
    ) {
        List<ExhibitRankingResponse> ranking = voteService.getTermRanking(term);
        return ResponseEntity.ok(ApiResponse.success("학기별 작품 랭킹 조회 성공", ranking));
    }

    /**
     * 상 계산/반영 트리거 (ADMIN 전용)
     */
    @PostMapping("/term/{term}/recalculate")
    public ResponseEntity<ApiResponse<List<ExhibitRankingResponse>>> recalculateAwards(
            @PathVariable String term
    ) {
        List<ExhibitRankingResponse> ranking = voteService.calculateAndApplyAwards(term);
        return ResponseEntity.ok(ApiResponse.success("상 재계산 완료", ranking));
    }

    /**
     * Admin: 특정 인기상 투표 삭제 (방문객/팀원 제거용)
     */
    @DeleteMapping("/popular/{voteId}")
    public ResponseEntity<ApiResponse<Void>> deletePopularVote(@PathVariable Long voteId) {
        voteService.deletePopularVote(voteId);
        return ResponseEntity.ok(ApiResponse.success("인기상 투표가 삭제되었습니다.", null));
    }

    /**
     * Admin: 특정 교수 평가 삭제
     */
    @DeleteMapping("/professor/{evalId}")
    public ResponseEntity<ApiResponse<Void>> deleteProfessorEvaluation(@PathVariable Long evalId) {
        voteService.deleteProfessorEvaluation(evalId);
        return ResponseEntity.ok(ApiResponse.success("교수 평가가 삭제되었습니다.", null));
    }
}