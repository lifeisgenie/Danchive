package DumbAndDumber.Danchive.api.dto.team;

public class TeamCreateResponse {
    private Long teamId;
    private String teamName;
    private String roleInTeam; // "leader"

    public TeamCreateResponse(Long teamId, String teamName, String roleInTeam) {
        this.teamId = teamId; this.teamName = teamName; this.roleInTeam = roleInTeam;
    }
    public Long getTeamId() { return teamId; }
    public String getTeamName() { return teamName; }
    public String getRoleInTeam() { return roleInTeam; }
}
