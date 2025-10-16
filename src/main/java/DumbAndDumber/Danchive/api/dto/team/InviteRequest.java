package DumbAndDumber.Danchive.api.dto.team;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record InviteRequest(
        @NotBlank @Email String email
) {}
