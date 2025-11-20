package DumbAndDumber.Danchive.api.dto.home.request;

import lombok.*; import jakarta.validation.constraints.NotBlank;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class NewsUpsertRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String summary;

    // 공지 상세 화면 링크 or 외부 링크
    private String link;
}