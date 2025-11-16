package DumbAndDumber.Danchive.api.dto.notice;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NoticeSummaryDto {
    private Long id;
    private String title;
    private LocalDateTime createdAt;
}