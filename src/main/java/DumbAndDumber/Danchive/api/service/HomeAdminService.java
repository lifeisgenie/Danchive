package DumbAndDumber.Danchive.api.service;

import DumbAndDumber.Danchive.api.dto.home.BannerDto;
import DumbAndDumber.Danchive.api.dto.home.NewsDto;
import DumbAndDumber.Danchive.api.dto.home.request.BannerUpsertRequest;
import DumbAndDumber.Danchive.api.dto.home.request.NewsUpsertRequest;
import DumbAndDumber.Danchive.api.repository.UserRepository;
import DumbAndDumber.Danchive.api.store.HomeInMemoryStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class HomeAdminService {

    private final HomeInMemoryStore store;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

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
}
