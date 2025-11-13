package DumbAndDumber.Danchive.api.controller;

import DumbAndDumber.Danchive.api.dto.ApiResponse;
import DumbAndDumber.Danchive.api.dto.exhibit.ExhibitAdminCreateRequest;
import DumbAndDumber.Danchive.api.dto.exhibit.ExhibitAdminUpdateRequest;
import DumbAndDumber.Danchive.api.service.ExhibitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exhibits")
public class ExhibitAdminController {

    private final ExhibitService exhibitService;

    // 지난 학기 포함 임의 학기 등록 (ADMIN 전용)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value="/admin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Map<String, Object>>> adminCreate(
            @RequestParam Long teamId,
            @Valid @ModelAttribute ExhibitAdminCreateRequest req
    ) {
        Long id = exhibitService.adminCreate(teamId, req); // term 명시 가능
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("작품이 등록되었습니다.", Map.of("exhibitId", id)));
    }

    // 수정 (학기/메타/파일 교체 등)
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value="/admin/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Map<String, Object>>> adminUpdate(
            @PathVariable Long id,
            @ModelAttribute ExhibitAdminUpdateRequest req
    ) {
        Long eid = exhibitService.adminUpdate(id, req);
        return ResponseEntity.ok(ApiResponse.success("작품이 수정되었습니다.", Map.of("exhibitId", eid)));
    }

    // 삭제
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> adminDelete(@PathVariable Long id) {
        exhibitService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // (선택) 일괄 등록: CSV + 이미지 ZIP
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value="/admin/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Map<String, Object>> bulkImport(
            @RequestParam("csv") MultipartFile csv,
            @RequestParam(value="imagesZip", required=false) MultipartFile imagesZip,
            @RequestParam(value="defaultTerm", required=false) String defaultTerm
    ) {
        int imported = exhibitService.adminBulkImport(csv, imagesZip, defaultTerm);
        return ApiResponse.success("작품 일괄 등록 완료", Map.of("imported", imported));
    }

    // (선택) 공개/비공개, 대표작/수상 플래그 토글 등
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/{id}/publish")
    public ApiResponse<Void> publish(@PathVariable Long id, @RequestParam boolean value) {
        exhibitService.setPublished(id, value);
        return ApiResponse.success("공개 상태가 변경되었습니다.");
    }
}