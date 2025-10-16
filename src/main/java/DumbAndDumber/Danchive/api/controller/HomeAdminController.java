package DumbAndDumber.Danchive.api.controller;

import DumbAndDumber.Danchive.api.dto.ApiResponse;
import DumbAndDumber.Danchive.api.dto.home.BannerDto;
import DumbAndDumber.Danchive.api.dto.home.NewsDto;
import DumbAndDumber.Danchive.api.dto.home.request.BannerUpsertRequest;
import DumbAndDumber.Danchive.api.dto.home.request.NewsUpsertRequest;
import DumbAndDumber.Danchive.api.service.HomeAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeAdminController {

    private final HomeAdminService adminService;

    // ===== BANNERS =====
    @GetMapping("/banners")
    public ResponseEntity<ApiResponse<List<BannerDto>>> listBanners() {
        return ResponseEntity.ok(ApiResponse.success("배너 목록 조회 성공", adminService.listBanners()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/banners")
    public ResponseEntity<ApiResponse<BannerDto>> createBanner(@RequestBody BannerUpsertRequest req) {
        return ResponseEntity.status(201).body(ApiResponse.success("배너가 생성되었습니다.", adminService.createBanner(req)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/banners/{id}")
    public ResponseEntity<ApiResponse<BannerDto>> updateBanner(@PathVariable Long id, @RequestBody BannerUpsertRequest req) {
        return ResponseEntity.ok(ApiResponse.success("배너가 수정되었습니다.", adminService.updateBanner(id, req)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/banners/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBanner(@PathVariable Long id) {
        adminService.deleteBanner(id);
        return ResponseEntity.ok(ApiResponse.success("배너가 삭제되었습니다."));
    }

    // ===== NEWS =====
    @GetMapping("/news")
    public ResponseEntity<ApiResponse<List<NewsDto>>> listNews() {
        return ResponseEntity.ok(ApiResponse.success("뉴스 목록 조회 성공", adminService.listNews()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/news")
    public ResponseEntity<ApiResponse<NewsDto>> createNews(@RequestBody NewsUpsertRequest req) {
        return ResponseEntity.status(201).body(ApiResponse.success("뉴스가 생성되었습니다.", adminService.createNews(req)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/news/{id}")
    public ResponseEntity<ApiResponse<NewsDto>> updateNews(@PathVariable Long id, @RequestBody NewsUpsertRequest req) {
        return ResponseEntity.ok(ApiResponse.success("뉴스가 수정되었습니다.", adminService.updateNews(id, req)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/news/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNews(@PathVariable Long id) {
        adminService.deleteNews(id);
        return ResponseEntity.ok(ApiResponse.success("뉴스가 삭제되었습니다."));
    }
}
