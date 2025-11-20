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
    private final NoticeService noticeService;

    // 홈 화면 전체 데이터
    @GetMapping("/")
    public ResponseEntity<ApiResponse<HomeResponse>> getHome() {
        return ResponseEntity.ok(
                ApiResponse.success("홈 데이터 조회 성공", homeService.fetchHomeData())
        );
    }

    // 공지 목록
    @GetMapping("/notices")
    public ApiResponse<List<NoticeSummaryDto>> listNotices() {
        return ApiResponse.success(
                "공지사항 목록 조회 성공",
                noticeService.listNotices()
        );
    }

    // 공지 상세
    @GetMapping("/notices/{id}")
    public ApiResponse<NoticeDetailResponse> getNotice(@PathVariable Long id) {
        return ApiResponse.success(
                "공지사항 조회 성공",
                noticeService.getNoticeDetail(id)
        );
    }
}