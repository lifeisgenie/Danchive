package DumbAndDumber.Danchive.api.dto.home.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsUpsertRequest {
    private String title;
    private String link;
    private String summary; // 옵션
    private String publishedAt; // ISO-8601 문자열 (없으면 now)
}
