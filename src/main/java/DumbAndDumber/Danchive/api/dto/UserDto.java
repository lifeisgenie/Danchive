package DumbAndDumber.Danchive.api.dto;

import DumbAndDumber.Danchive.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String name;
    private String role;
    private String department;

    public static UserDto of(User u) {
        return new UserDto(u.getId(), u.getEmail(), u.getName(), u.getRole(), u.getDepartment());
    }
}
