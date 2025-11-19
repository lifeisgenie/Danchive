package DumbAndDumber.Danchive.api.controller;

import DumbAndDumber.Danchive.api.dto.ApiResponse;
import DumbAndDumber.Danchive.api.dto.notice.NoticeDetailResponse;
import DumbAndDumber.Danchive.api.dto.notice.NoticeSummaryDto;
import DumbAndDumber.Danchive.api.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notices")
public class NoticeController {

    private final NoticeService noticeService;

    // 공지 목록
    @GetMapping
    public ApiResponse<List<NoticeSummaryDto>> listNotices() {
        return ApiResponse.success(
                "공지사항 목록 조회 성공",
                noticeService.listNotices()
        );
    }

    // 공지 상세
    @GetMapping("/{id}")
    public ApiResponse<NoticeDetailResponse> getNotice(@PathVariable Long id) {
        return ApiResponse.success(
                "공지사항 조회 성공",
                noticeService.getNoticeDetail(id)
        );
    }
}