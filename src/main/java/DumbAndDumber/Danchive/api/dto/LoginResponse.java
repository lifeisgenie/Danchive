package DumbAndDumber.Danchive.api.dto;

import DumbAndDumber.Danchive.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponse {
    private String access_token;
    private String refresh_token;
    private User user;
    public LoginResponse(String access, String refresh, User user) {
        this.access_token = access;
        this.refresh_token = refresh;
        this.user = user;
    }
}
