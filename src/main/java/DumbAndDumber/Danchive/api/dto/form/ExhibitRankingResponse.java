package DumbAndDumber.Danchive.api.dto.form;

import DumbAndDumber.Danchive.api.entity.ExhibitAward;
import DumbAndDumber.Danchive.api.entity.ExhibitCategory;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class ExhibitRankingResponse {

    private Long id;
    private String title;
    private String teamName;
    private String term;

    private Set<ExhibitCategory> categories;
    private Set<ExhibitAward> awards;

    private double avgProfessorScore;   // 교수 평가 평균 (0~5, 없으면 0)
    private double avgPopularScore;     // 인기 투표 평균 (0~5, 없으면 0)
    private long popularVoteCount;      // 인기 투표 수

    private LocalDateTime createdAt;    // 등록일 (정렬용)
}