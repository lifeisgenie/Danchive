package DumbAndDumber.Danchive.api.dto.user;

import DumbAndDumber.Danchive.api.entity.User;

public record UserDto(Long id, String email, String name, String role, String department) {
    public static UserDto of(User u) {
        return new UserDto(
                u.getId(),
                u.getEmail(),
                u.getName(),
                u.getRole().name().toLowerCase(), // "TEAM" -> "team"
                u.getDepartment()
        );
    }
}