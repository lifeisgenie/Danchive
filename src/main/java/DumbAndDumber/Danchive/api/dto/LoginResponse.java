package DumbAndDumber.Danchive.api.dto;

import DumbAndDumber.Danchive.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    private String access_token;
    private String refresh_token;
    private User user;
}
