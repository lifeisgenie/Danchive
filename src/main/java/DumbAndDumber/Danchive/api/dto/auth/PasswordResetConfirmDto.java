package DumbAndDumber.Danchive.api.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetConfirmDto {
    private String reset_token;
    private String new_password;
}
