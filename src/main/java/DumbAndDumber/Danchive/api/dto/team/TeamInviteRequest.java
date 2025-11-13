package DumbAndDumber.Danchive.api.dto.team;

import jakarta.validation.constraints.Email; import jakarta.validation.constraints.NotBlank;
import lombok.Getter; @Getter

public class TeamInviteRequest { @Email @NotBlank private String email; }