package DumbAndDumber.Danchive.api.dto.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PopularVoteRequest {

    @NotNull
    private Long exhibitId; // 투표 대상 작품 ID

    @NotBlank
    private String favoriteTeamName; // 주관식 팀 이름

    @Min(1) @Max(5)
    private int creativityScore;     // 아이디어/창의성

    @Min(1) @Max(5)
    private int completionScore;     // 완성도/주제 전달력

    // 방문객용 (선택)
    private String voterName;
    private String voterContact;
}