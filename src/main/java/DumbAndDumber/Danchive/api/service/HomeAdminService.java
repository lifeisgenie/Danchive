package DumbAndDumber.Danchive.api.service;

import DumbAndDumber.Danchive.api.dto.home.BannerDto;
import DumbAndDumber.Danchive.api.dto.home.NewsDto;
import DumbAndDumber.Danchive.api.dto.home.request.BannerUpsertRequest;
import DumbAndDumber.Danchive.api.dto.home.request.NewsUpsertRequest;
import DumbAndDumber.Danchive.api.store.HomeInMemoryStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeAdminService {

    private final HomeInMemoryStore store;

    // Banners
    public List<BannerDto> listBanners() { return store.listBanners(); }
    public BannerDto createBanner(BannerUpsertRequest req) {
        return store.createBanner(BannerDto.builder()
                .title(req.getTitle())
                .imageUrl(req.getImageUrl())
                .linkUrl(req.getLinkUrl())
                .altText(req.getAltText())
                .build());
    }
    public BannerDto updateBanner(Long id, BannerUpsertRequest req) {
        return store.updateBanner(id, BannerDto.builder()
                .id(id)
                .title(req.getTitle())
                .imageUrl(req.getImageUrl())
                .linkUrl(req.getLinkUrl())
                .altText(req.getAltText())
                .build()).orElseThrow(() -> new IllegalArgumentException("배너를 찾을 수 없습니다."));
    }
    public void deleteBanner(Long id) {
        if (!store.deleteBanner(id)) throw new IllegalArgumentException("배너를 찾을 수 없습니다.");
    }

    // News
    public List<NewsDto> listNews() { return store.listNews(); }
    public NewsDto createNews(NewsUpsertRequest req) {
        return store.createNews(NewsDto.builder()
                .title(req.getTitle())
                .link(req.getLink())
                .summary(req.getSummary())
                .publishedAt(parseOrNow(req.getPublishedAt()))
                .build());
    }
    public NewsDto updateNews(Long id, NewsUpsertRequest req) {
        return store.updateNews(id, NewsDto.builder()
                .id(id)
                .title(req.getTitle())
                .link(req.getLink())
                .summary(req.getSummary())
                .publishedAt(parseOrNow(req.getPublishedAt()))
                .build()).orElseThrow(() -> new IllegalArgumentException("뉴스를 찾을 수 없습니다."));
    }
    public void deleteNews(Long id) {
        if (!store.deleteNews(id)) throw new IllegalArgumentException("뉴스를 찾을 수 없습니다.");
    }

    private Instant parseOrNow(String iso) {
        if (iso == null || iso.isBlank()) return Instant.now();
        try { return Instant.parse(iso); }
        catch (DateTimeParseException e) { return Instant.now(); }
    }
}
