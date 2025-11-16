package DumbAndDumber.Danchive.api.dto.notice;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NoticeUpdateRequest {
    @Size(max = 150)
    private String title;
    private String content;
}