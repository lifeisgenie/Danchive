package DumbAndDumber.Danchive.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TokenRefreshRequest {
    private String refresh_token;
}
