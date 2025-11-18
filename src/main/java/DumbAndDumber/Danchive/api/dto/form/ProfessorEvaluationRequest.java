package DumbAndDumber.Danchive.api.dto.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProfessorEvaluationRequest {

    @NotNull
    private Long exhibitId;

    @Min(1) @Max(5)
    private int technicalScore;      // 기술적 완성도 / 목표 구현

    @Min(1) @Max(5)
    private int impactScore;         // 문제 중요성 & 실용적 가치

    @Min(1) @Max(5)
    private int creativityScore;     // 아이디어 혁신성

    private String comment;
}