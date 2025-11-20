package DumbAndDumber.Danchive.api.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProfessorRequest {
    private String email;
    private String password;
    private String name;
    private String department;
    private String studentId;
}