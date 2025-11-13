package DumbAndDumber.Danchive.api.dto.home.request;

import lombok.*; import jakarta.validation.constraints.NotBlank;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NewsUpsertRequest {
    @NotBlank private String title;
    @NotBlank private String link;
    private String summary;
    /** ISO-8601 (선택) */
    private String publishedAt;
}