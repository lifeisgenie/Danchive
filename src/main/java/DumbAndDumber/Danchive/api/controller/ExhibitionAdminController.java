package DumbAndDumber.Danchive.api.controller;

import DumbAndDumber.Danchive.api.dto.ApiResponse;
import DumbAndDumber.Danchive.api.dto.exhibit.ExhibitionCreateRequest;
import DumbAndDumber.Danchive.api.dto.exhibit.ExhibitionUpdateRequest;
import DumbAndDumber.Danchive.api.service.ExhibitionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exhibitions")
public class ExhibitionAdminController {

    private final ExhibitionService exhibitionService;

    // 전시회 생성 (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/admin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Map<String, Object>>> create(
            @Valid @ModelAttribute ExhibitionCreateRequest req
    ) {
        Long id = exhibitionService.createExhibition(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("전시회가 등록되었습니다.", Map.of("exhibitionId", id)));
    }

    // 전시회 수정 (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/admin/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Map<String, Object>> update(
            @PathVariable Long id,
            @ModelAttribute ExhibitionUpdateRequest req
    ) {
        Long eid = exhibitionService.updateExhibition(id, req);
        return ApiResponse.success("전시회가 수정되었습니다.", Map.of("exhibitionId", eid));
    }

    // 전시회 삭제 (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        exhibitionService.deleteExhibition(id);
        return ResponseEntity.noContent().build();
    }
}