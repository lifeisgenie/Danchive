package DumbAndDumber.Danchive.api.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RegisterRequest {
    private String email;
    private String password;
    private String name;
    private String role;
    private String department;
}