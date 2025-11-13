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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@RestController @RequiredArgsConstructor @RequestMapping("/api/v1")
public class ExhibitController {
    private final ExhibitService exhibitService;

    @GetMapping("/categories")
    public ApiResponse<List<String>> categories() {
        return ApiResponse.success("카테고리 조회 성공",
                Arrays.stream(ExhibitCategory.values()).map(Enum::name).toList());
    }

    @GetMapping("/exhibits/terms")
    public ApiResponse<TermsResponse> terms() {
        List<String> terms = exhibitService.getTerms(); // DESC
        String current = terms.isEmpty() ? computeCurrentTerm() : terms.get(0);
        return ApiResponse.success("학기 목록 조회 성공", new TermsResponse(current, terms));
    }

    private String computeCurrentTerm() {
        var today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        int y = today.getYear(), m = today.getMonthValue();
        int s = (m >= 3 && m <= 8) ? 1 : 2;
        if (s == 2 && m <= 2) y -= 1; // 1~2월은 전년도-2학기로 본다
        return y + "-" + s;
    }

    @GetMapping("/exhibits")
    public ApiResponse<Map<String, Object>> list(
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

        Page<ExhibitListItemDto> result = exhibitService.search(term, category, q, awardOnly, pageable);
        Map<String, Object> data = new HashMap<>();
        data.put("content", result.getContent());
        data.put("page", result.getNumber());
        data.put("size", result.getSize());
        data.put("totalElements", result.getTotalElements());
        data.put("totalPages", result.getTotalPages());
        data.put("sort", sort);
        return ApiResponse.success("작품 목록 조회 성공", data);
    }

    @GetMapping("/exhibits/{id}")
    public ApiResponse<ExhibitDetailDto> detail(@PathVariable Long id) {
        return ApiResponse.success("작품 상세 조회 성공", exhibitService.get(id));
    }

    @PostMapping(value="/exhibits", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('TEAM')") // 리더 검증은 Service에서
    public ResponseEntity<ApiResponse<Map<String, Object>>> create(@RequestParam Long teamId, @Valid @ModelAttribute ExhibitCreateRequest req) {
        Long id = exhibitService.create(teamId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("작품이 등록되었습니다.", Map.of("exhibitId", id)));
    }

    @PatchMapping(value="/exhibits/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('TEAM')")
    public ApiResponse<Map<String, Object>> update(@PathVariable Long id, @ModelAttribute ExhibitUpdateRequest req) {
        Long eid = exhibitService.update(id, req);
        return ApiResponse.success("작품이 수정되었습니다.", Map.of("exhibitId", eid));
    }

    @DeleteMapping("/exhibits/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        exhibitService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/exhibits/{id}/view")
    public ResponseEntity<Void> view(@PathVariable Long id) {
        exhibitService.increaseViews(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/exhibits/{id}/like")
    @PreAuthorize("hasAnyRole('GUEST','TEAM','PROF','ADMIN')")
    public ApiResponse<Map<String, Object>> like(@PathVariable Long id) {
        return ApiResponse.success("좋아요가 반영되었습니다.", exhibitService.like(id));
    }

    @DeleteMapping("/exhibits/{id}/like")
    @PreAuthorize("hasAnyRole('GUEST','TEAM','PROF','ADMIN')")
    public ApiResponse<Map<String, Object>> unlike(@PathVariable Long id) {
        return ApiResponse.success("좋아요가 취소되었습니다.", exhibitService.unlike(id));
    }
}