package DumbAndDumber.Danchive.api.service;

import DumbAndDumber.Danchive.api.dto.home.BannerDto;
import DumbAndDumber.Danchive.api.dto.home.HomeResponse;
import DumbAndDumber.Danchive.api.dto.home.NavItemDto;
import DumbAndDumber.Danchive.api.dto.home.NewsDto;
import DumbAndDumber.Danchive.api.store.HomeInMemoryStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final HomeInMemoryStore store;

    public HomeResponse fetchHomeData() {
        List<BannerDto> banners = store.listBanners();
        List<NewsDto> news = store.listNews();
        List<NavItemDto> nav = List.of(
                NavItemDto.builder().key("home").title("홈").path("/").build(),
                NavItemDto.builder().key("browse").title("조회").path("/browse").build(),
                NavItemDto.builder().key("exhibit").title("전시회").path("/exhibits").build(),
                NavItemDto.builder().key("mypage").title("마이페이지").path("/me").build()
        );
        return HomeResponse.builder().banners(banners).news(news).nav(nav).build();
    }
}
