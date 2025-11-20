package DumbAndDumber.Danchive.api.store;

import DumbAndDumber.Danchive.api.dto.home.BannerDto;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class HomeInMemoryStore {

    private final Map<Long, BannerDto> banners = new ConcurrentHashMap<>();
    private final AtomicLong bannerSeq = new AtomicLong(0);

    @PostConstruct
    public void seed() {
        // 초기 배너 더미
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

    public Optional<BannerDto> getBanner(Long id) {
        return Optional.ofNullable(banners.get(id));
    }

    public Optional<BannerDto> updateBanner(Long id, BannerDto dto) {
        if (!banners.containsKey(id)) return Optional.empty();
        dto.setId(id);
        banners.put(id, dto);
        return Optional.of(dto);
    }

    public boolean deleteBanner(Long id) {
        return banners.remove(id) != null;
    }
}