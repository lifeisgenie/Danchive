package DumbAndDumber.Danchive.api.controller;

import DumbAndDumber.Danchive.api.dto.ApiResponse;
import DumbAndDumber.Danchive.api.dto.home.HomeResponse;
import DumbAndDumber.Danchive.api.dto.notice.NoticeDetailResponse;
import DumbAndDumber.Danchive.api.dto.notice.NoticeSummaryDto;
import DumbAndDumber.Danchive.api.service.HomeService;
import DumbAndDumber.Danchive.api.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/home")
public class HomeController {

    private final HomeService homeService;

    // 홈 화면 전체 데이터
    @GetMapping
    public ResponseEntity<ApiResponse<HomeResponse>> getHome() {
        return ResponseEntity.ok(
                ApiResponse.success("홈 데이터 조회 성공", homeService.fetchHomeData())
        );
    }
}