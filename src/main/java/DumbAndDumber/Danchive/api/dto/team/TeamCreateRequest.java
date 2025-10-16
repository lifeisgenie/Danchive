package DumbAndDumber.Danchive.api.dto.team;

import jakarta.validation.constraints.NotBlank;

public class TeamCreateRequest {
    @NotBlank
    private String teamName;
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
}
