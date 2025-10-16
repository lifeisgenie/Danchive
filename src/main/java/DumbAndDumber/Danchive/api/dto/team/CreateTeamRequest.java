package DumbAndDumber.Danchive.api.dto.team;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTeamRequest(
        @NotBlank @Size(max=100) String teamName
) {}
