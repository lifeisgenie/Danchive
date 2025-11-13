package DumbAndDumber.Danchive.api.dto.team;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter; @Getter

public class TeamCreateRequest { @NotBlank private String teamName; }