package DumbAndDumber.Danchive.api.dto.home;

import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NavItemDto {
    private String key;
    private String title;
    private String path;
}