package DumbAndDumber.Danchive.api.controller;

import DumbAndDumber.Danchive.api.dto.ApiResponse;
import DumbAndDumber.Danchive.api.dto.home.*;
import DumbAndDumber.Danchive.api.dto.home.request.*;
import DumbAndDumber.Danchive.api.dto.notice.NoticeCreateRequest;
import DumbAndDumber.Danchive.api.dto.notice.NoticeDetailResponse;
import DumbAndDumber.Danchive.api.dto.notice.NoticeSummaryDto;
import DumbAndDumber.Danchive.api.dto.notice.NoticeUpdateRequest;
import DumbAndDumber.Danchive.api.service.HomeAdminService;
import DumbAndDumber.Danchive.api.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/home")
public class HomeAdminController {

    private final HomeAdminService adminService;
    private final NoticeService noticeService;

    // ===== 배너 =====

    @GetMapping("/banners")
    public ResponseEntity<ApiResponse<List<BannerDto>>> listBanners(){
        return ResponseEntity.ok(
                ApiResponse.success("배너 목록 조회 성공", adminService.listBanners())
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/banners")
    public ResponseEntity<ApiResponse<BannerDto>> createBanner(@RequestBody BannerUpsertRequest req){
        return ResponseEntity.status(201)
                .body(ApiResponse.success("배너가 생성되었습니다.", adminService.createBanner(req)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/banners/{id}")
    public ResponseEntity<ApiResponse<BannerDto>> updateBanner(@PathVariable Long id,
                                                               @RequestBody BannerUpsertRequest req){
        return ResponseEntity.ok(
                ApiResponse.success("배너가 수정되었습니다.", adminService.updateBanner(id, req))
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/banners/{id}")
    public ResponseEntity<Void> deleteBanner(@PathVariable Long id){
        adminService.deleteBanner(id);
        return ResponseEntity.noContent().build();
    }

    // ===== 공지 (관리자 전용) =====

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/notices")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createNotice(
            @RequestBody @Valid NoticeCreateRequest req
    ) {
        Long id = noticeService.createNotice(req); // 여기서 FCM 발송
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("공지사항이 등록되었습니다.", Map.of("noticeId", id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/notices/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateNotice(
            @PathVariable Long id,
            @RequestBody @Valid NoticeUpdateRequest req
    ) {
        Long nid = noticeService.updateNotice(id, req);
        return ResponseEntity.ok(
                ApiResponse.success("공지사항이 수정되었습니다.", Map.of("noticeId", nid))
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/notices/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return ResponseEntity.ok(ApiResponse.success("공지사항이 삭제되었습니다."));
    }
}