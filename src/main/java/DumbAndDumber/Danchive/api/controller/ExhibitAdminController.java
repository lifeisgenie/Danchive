package DumbAndDumber.Danchive.api.controller;

import DumbAndDumber.Danchive.api.dto.ApiResponse;
import DumbAndDumber.Danchive.api.dto.exhibit.ExhibitAdminUpdateRequest;
import DumbAndDumber.Danchive.api.dto.exhibit.ExhibitCreateRequest;
import DumbAndDumber.Danchive.api.dto.exhibit.ExhibitListItemDto;
import DumbAndDumber.Danchive.api.entity.ExhibitCategory;
import DumbAndDumber.Danchive.api.service.ExhibitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exhibits")
public class ExhibitAdminController {

    private final ExhibitService exhibitService;

    // 임의 학기 등록
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value="/admin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Map<String, Object>>> adminCreate(
            @RequestParam Long teamId,
            @Valid @ModelAttribute ExhibitCreateRequest req
    ) {
        Long id = exhibitService.adminCreate(teamId, req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("작품이 등록되었습니다.", Map.of("exhibitId", id)));
    }

    // 임의 항목 수정 (학기/공개여부 포함)
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value="/admin/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Map<String, Object>> adminUpdate(
            @PathVariable Long id,
            @ModelAttribute ExhibitAdminUpdateRequest req
    ) {
        Long eid = exhibitService.adminUpdate(id, req);
        return ApiResponse.success("작품이 수정되었습니다.", Map.of("exhibitId", eid));
    }

    // 공개/비공개 토글
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/{id}/publish")
    public ApiResponse<Void> setPublished(@PathVariable Long id, @RequestParam boolean value) {
        exhibitService.setPublished(id, value);
        return ApiResponse.success("공개 상태가 변경되었습니다.");
    }

    // (선택) 관리자 목록: 공개/비공개 포함
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ApiResponse<Map<String, Object>> adminList(
            @RequestParam String term,
            @RequestParam(required=false) Set<ExhibitCategory> category,
            @RequestParam(required=false) String q,
            @RequestParam(required=false, defaultValue="false") boolean awardOnly,
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="20") int size,
            @RequestParam(required=false, defaultValue="createdAt,desc") String sort
    ) {
        String[] sp = sort.split(",");
        Sort s = Sort.by(sp[0]).descending();
        if (sp.length>1 && "asc".equalsIgnoreCase(sp[1])) s = Sort.by(sp[0]).ascending();
        Pageable pageable = PageRequest.of(page, size, s);

        Page<ExhibitListItemDto> result = exhibitService.search(term, category, q, awardOnly, pageable, false);

        Map<String, Object> data = new HashMap<>();
        data.put("content", result.getContent());
        data.put("page", result.getNumber());
        data.put("size", result.getSize());
        data.put("totalElements", result.getTotalElements());
        data.put("totalPages", result.getTotalPages());
        data.put("sort", sort);
        return ApiResponse.success("관리자 작품 목록 조회 성공", data);
    }
}