package DumbAndDumber.Danchive.api.dto.home;

import lombok.*; import java.util.List;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HomeResponse {
    private List<BannerDto> banners;
    private List<NewsDto> news;
    private List<NavItemDto> nav;
}