package DumbAndDumber.Danchive.api.dto.home;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class HomeResponse {
    private List<BannerDto> banners;
    private List<NewsDto> news;
    private List<NavItemDto> nav; // 홈, 조회, 전시회, 마이페이지
}