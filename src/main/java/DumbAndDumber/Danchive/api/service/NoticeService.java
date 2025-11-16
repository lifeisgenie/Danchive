package DumbAndDumber.Danchive.api.service;

import DumbAndDumber.Danchive.api.dto.notice.NoticeCreateRequest;
import DumbAndDumber.Danchive.api.dto.notice.NoticeDetailResponse;
import DumbAndDumber.Danchive.api.dto.notice.NoticeSummaryDto;
import DumbAndDumber.Danchive.api.dto.notice.NoticeUpdateRequest;
import DumbAndDumber.Danchive.api.entity.Notice;
import DumbAndDumber.Danchive.api.entity.User;
import DumbAndDumber.Danchive.api.repository.NoticeRepository;
import DumbAndDumber.Danchive.api.repository.UserRepository;
import DumbAndDumber.Danchive.api.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<NoticeSummaryDto> listNotices() {
        return noticeRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(n -> NoticeSummaryDto.builder()
                        .id(n.getId())
                        .title(n.getTitle())
                        .createdAt(n.getCreatedAt())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public NoticeDetailResponse getNoticeDetail(Long id) {
        Notice n = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("notice not found"));

        return NoticeDetailResponse.builder()
                .id(n.getId())
                .title(n.getTitle())
                .content(n.getContent())
                .createdAt(n.getCreatedAt())
                .createdByName(n.getCreatedBy() != null ? n.getCreatedBy().getName() : null)
                .build();
    }

    public Long createNotice(NoticeCreateRequest req) {
        User admin = SecurityUtil.getCurrentUserOrThrow(); // ADMIN이어야 함 (컨트롤러에서 PreAuthorize)

        Notice notice = Notice.builder()
                .title(req.getTitle())
                .content(req.getContent())
                .createdAt(LocalDateTime.now())
                .createdBy(admin)
                .build();

        Notice saved = noticeRepository.save(notice);

        // FCM 발송: fcmToken이 있는 모든 유저
        List<String> tokens = userRepository.findAllByFcmTokenIsNotNull()
                .stream()
                .map(User::getFcmToken)
                .toList();

        notificationService.sendNoticeToTokens(
                tokens,
                "새 공지사항이 등록되었습니다.",
                req.getTitle(),
                Map.of(
                        "type", "notice",
                        "noticeId", saved.getId().toString()
                )
        );

        return saved.getId();
    }

    public Long updateNotice(Long id, NoticeUpdateRequest req) {
        Notice n = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("notice not found"));

        if (req.getTitle() != null) n.setTitle(req.getTitle());
        if (req.getContent() != null) n.setContent(req.getContent());

        return n.getId();
    }

    public void deleteNotice(Long id) {
        noticeRepository.deleteById(id);
    }
}