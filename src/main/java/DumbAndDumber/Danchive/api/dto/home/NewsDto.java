package DumbAndDumber.Danchive.api.dto.home;

import lombok.*; import java.time.Instant;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NewsDto {
    private Long id;
    private String title;
    private String link;
    private String summary;
    private Instant publishedAt;
}