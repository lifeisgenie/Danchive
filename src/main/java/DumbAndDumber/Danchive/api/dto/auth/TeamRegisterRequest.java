package DumbAndDumber.Danchive.api.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TeamRegisterRequest {
    private String email;
    private String password;
    private String name;
    private String department;
    private String studentId;
}