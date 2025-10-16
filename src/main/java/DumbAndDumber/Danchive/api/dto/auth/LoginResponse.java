package DumbAndDumber.Danchive.api.dto.auth;

import DumbAndDumber.Danchive.api.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String access_token;
    private UserDto user;
}