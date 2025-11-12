package DumbAndDumber.Danchive.api.controller;

import DumbAndDumber.Danchive.api.dto.ApiResponse;
import DumbAndDumber.Danchive.api.dto.exhibit.*;
import DumbAndDumber.Danchive.api.entity.ExhibitCategory;
import DumbAndDumber.Danchive.api.service.ExhibitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class ExhibitController {

    private final ExhibitService exhibitService;

    // --- 카테고리 ---
    @GetMapping("/categories")
    public ApiResponse<List<String>> categories() {
        return ApiResponse.success("카테고리 조회 성공",
                Arrays.stream(ExhibitCategory.values()).map(Enum::name).toList());
    }

    // --- 학기 목록 ---
    @GetMapping("/exhibits/terms")
    public ApiResponse<Map<String, Object>> terms() {
        List<String> terms = exhibitService.getTerms();
        return ApiResponse.success("학기 목록 조회 성공", Map.of("terms", terms));
    }

    // --- 목록 ---
    @GetMapping("/exhibits")
    public ApiResponse<Map<String, Object>> list(
            @RequestParam String term,
            @RequestParam(required = false) Set<ExhibitCategory> category,
            @RequestParam(required = false) String q,
            @RequestParam(required = false, defaultValue = "false") boolean awardOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "createdAt,desc") String sort
    ) {
        Sort s = Sort.by(sort.split(",")[0]).descending();
        if (sort.endsWith(",asc")) s = Sort.by(sort.split(",")[0]).ascending();
        Pageable pageable = PageRequest.of(page, size, s);

        Page<ExhibitListItemDto> result = exhibitService.search(term, category, q, awardOnly, pageable);
        Map<String, Object> data = new HashMap<>();
        data.put("content", result.getContent());
        data.put("page", result.getNumber());
        data.put("size", result.getSize());
        data.put("totalElements", result.getTotalElements());
        return ApiResponse.success("작품 목록 조회 성공", data);
    }

    // --- 상세 ---
    @GetMapping("/exhibits/{id}")
    public ApiResponse<ExhibitDetailDto> detail(@PathVariable Long id) {
        return ApiResponse.success("작품 상세 조회 성공", exhibitService.get(id));
    }

    // --- 등록: 팀장만 ---
    @PostMapping(value="/exhibits", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('TEAM_LEADER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> create(
            @RequestParam Long teamId,
            @Valid @ModelAttribute ExhibitCreateRequest req
    ) {
        Long id = exhibitService.create(teamId, req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("작품이 등록되었습니다.", Map.of("exhibitId", id)));
    }

    // --- 수정: 팀원/팀장 (파일교체는 팀장만) ---
    @PatchMapping(value="/exhibits/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('TEAM_LEADER','TEAM_MEMBER')")
    public ApiResponse<Map<String, Object>> update(
            @PathVariable Long id,
            @ModelAttribute ExhibitUpdateRequest req
    ) {
        Long eid = exhibitService.update(id, req);
        return ApiResponse.success("작품이 수정되었습니다.", Map.of("exhibitId", eid));
    }

    // --- 삭제: admin ---
    @DeleteMapping("/exhibits/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        exhibitService.delete(id);
        return ApiResponse.success("작품이 삭제되었습니다.", null);
    }

    // --- 조회수 ---
    @PostMapping("/exhibits/{id}/view")
    public ApiResponse<Map<String, Object>> view(@PathVariable Long id) {
        long v = exhibitService.increaseViews(id);
        return ApiResponse.success("조회수가 반영되었습니다.", Map.of("views", v));
    }

    // --- 좋아요 등록 ---
    @PostMapping("/exhibits/{id}/like")
    @PreAuthorize("hasAnyRole('GUEST','TEAM_LEADER','TEAM_MEMBER','PROF')")
    public ApiResponse<Map<String, Object>> like(@PathVariable Long id) {
        return ApiResponse.success("좋아요가 반영되었습니다.", exhibitService.like(id));
    }

    // --- 좋아요 취소 ---
    @DeleteMapping("/exhibits/{id}/like")
    @PreAuthorize("hasAnyRole('GUEST','TEAM_LEADER','TEAM_MEMBER','PROF')")
    public ApiResponse<Map<String, Object>> unlike(@PathVariable Long id) {
        return ApiResponse.success("좋아요가 취소되었습니다.", exhibitService.unlike(id));
    }
}
