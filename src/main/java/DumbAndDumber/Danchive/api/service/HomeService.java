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
    private final NoticeService noticeService;

    public HomeResponse fetchHomeData() {
        // 배너: 인메모리 더미 or 관리 API
        List<BannerDto> banners = store.listBanners();

        // 뉴스: 최근 공지 3개 → NewsDto 변환
        List<NewsDto> news = noticeService.getLatestNews(3);

        // 내비: 고정값 (필요에 따라 수정)
        List<NavItemDto> nav = List.of(
                NavItemDto.builder()
                        .key("exhibits")
                        .title("작품 보기")
                        .path("/exhibits")
                        .build(),
                NavItemDto.builder()
                        .key("awards")
                        .title("수상작")
                        .path("/exhibits?awardOnly=true")
                        .build(),
                NavItemDto.builder()
                        .key("notice")
                        .title("공지사항")
                        .path("/notices")
                        .build()
        );

        return HomeResponse.builder()
                .banners(banners)
                .news(news)
                .nav(nav)
                .build();
    }
}