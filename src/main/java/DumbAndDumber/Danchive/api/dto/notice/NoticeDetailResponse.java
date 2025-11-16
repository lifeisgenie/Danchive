package DumbAndDumber.Danchive.api.dto.notice;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NoticeDetailResponse {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String createdByName;
}