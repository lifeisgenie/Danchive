package DumbAndDumber.Danchive.api.dto.home;

import lombok.*;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class NavItemDto {
    private String key;   // home|browse|exhibit|mypage
    private String title; // 홈|조회|전시회|마이페이지
    private String path;  // / | /browse | /exhibits | /me
}
