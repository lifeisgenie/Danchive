package DumbAndDumber.Danchive.api.dto.home;

import lombok.*;

import java.time.Instant;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class NewsDto {
    private Long id;
    private String title;
    private String link;
    private Instant publishedAt;
    private String summary;
}
