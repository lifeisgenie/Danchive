package DumbAndDumber.Danchive.api.store;

import DumbAndDumber.Danchive.api.dto.home.BannerDto;
import DumbAndDumber.Danchive.api.dto.home.NewsDto;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class HomeInMemoryStore {

    private final Map<Long, BannerDto> banners = new ConcurrentHashMap<>();
    private final Map<Long, NewsDto> news = new ConcurrentHashMap<>();
    private final AtomicLong bannerSeq = new AtomicLong(0);
    private final AtomicLong newsSeq = new AtomicLong(0);

    @PostConstruct
    public void seed() {
        // 초기 배너/뉴스 더미
        createBanner(BannerDto.builder()
                .title("2025-2 졸업작품전시회 안내")
                .imageUrl("https://cdn.example.com/banners/2025-2.jpg")
                .linkUrl("/exhibits?term=2025-2")
                .altText("2025년 2학기 졸업작품전시회 배너").build());

        createBanner(BannerDto.builder()
                .title("수상작 모아보기")
                .imageUrl("https://cdn.example.com/banners/awards.jpg")
                .linkUrl("/exhibits?term=2025-2&awardOnly=true")
                .altText("수상작 모아보기").build());

        createNews(NewsDto.builder()
                .title("[공지] 전시 일정 및 장소 안내")
                .link("/news/10")
                .summary("2025-2 전시회 일정 및 장소를 안내드립니다.")
                .publishedAt(Instant.now().minusSeconds(3600)).build());

        createNews(NewsDto.builder()
                .title("[Tip] PPT 미리보기 업로드 가이드")
                .link("/news/11")
                .summary("PPT 업로드 및 미리보기 생성 방법을 확인하세요.")
                .publishedAt(Instant.now().minusSeconds(7200)).build());
    }

    // ===== Banners =====
    public List<BannerDto> listBanners() {
        return banners.values().stream()
                .sorted(Comparator.comparingLong(BannerDto::getId))
                .toList();
    }
    public BannerDto createBanner(BannerDto dto) {
        long id = bannerSeq.incrementAndGet();
        dto.setId(id);
        banners.put(id, dto);
        return dto;
    }
    public Optional<BannerDto> getBanner(Long id) { return Optional.ofNullable(banners.get(id)); }
    public Optional<BannerDto> updateBanner(Long id, BannerDto dto) {
        if (!banners.containsKey(id)) return Optional.empty();
        dto.setId(id);
        banners.put(id, dto);
        return Optional.of(dto);
    }
    public boolean deleteBanner(Long id) { return banners.remove(id) != null; }

    // ===== News =====
    public List<NewsDto> listNews() {
        return news.values().stream()
                .sorted(Comparator.comparing(NewsDto::getPublishedAt).reversed())
                .toList();
    }
    public NewsDto createNews(NewsDto dto) {
        long id = newsSeq.incrementAndGet();
        dto.setId(id);
        if (dto.getPublishedAt() == null) dto.setPublishedAt(Instant.now());
        news.put(id, dto);
        return dto;
    }
    public Optional<NewsDto> getNews(Long id) { return Optional.ofNullable(news.get(id)); }
    public Optional<NewsDto> updateNews(Long id, NewsDto dto) {
        if (!news.containsKey(id)) return Optional.empty();
        dto.setId(id);
        if (dto.getPublishedAt() == null) dto.setPublishedAt(Instant.now());
        news.put(id, dto);
        return Optional.of(dto);
    }
    public boolean deleteNews(Long id) { return news.remove(id) != null; }
}