package DumbAndDumber.Danchive.api.dto.home;

import lombok.*;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class BannerDto {
    private Long id;
    private String title;
    private String imageUrl;   // 배너 이미지
    private String linkUrl;    // 클릭 시 이동 경로(선택)
    private String altText;    // 접근성 텍스트
}
