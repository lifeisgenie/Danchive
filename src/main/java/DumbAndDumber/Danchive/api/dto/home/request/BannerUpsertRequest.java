package DumbAndDumber.Danchive.api.dto.home.request;

import lombok.*; import jakarta.validation.constraints.NotBlank;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BannerUpsertRequest {
    @NotBlank private String title;
    @NotBlank private String imageUrl;
    private String linkUrl;
    private String altText;
}