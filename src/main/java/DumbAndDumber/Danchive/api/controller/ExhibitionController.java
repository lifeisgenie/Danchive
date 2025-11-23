package DumbAndDumber.Danchive.api.controller;

import DumbAndDumber.Danchive.api.dto.ApiResponse;
import DumbAndDumber.Danchive.api.dto.exhibit.ExhibitionSummaryDto;
import DumbAndDumber.Danchive.api.service.ExhibitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exhibitions")
public class ExhibitionController {

    private final ExhibitionService exhibitionService;

    /**
     * 전시회 목록 조회 (학기별 1개씩)
     * 권한: 전체 공개
     */
    @GetMapping
    public ApiResponse<List<ExhibitionSummaryDto>> listExhibitions() {
        return ApiResponse.success(
                "전시회 목록 조회 성공",
                exhibitionService.getExhibitions()
        );
    }
}