package DumbAndDumber.Danchive.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private String access_token;
    private UserDto user;
}