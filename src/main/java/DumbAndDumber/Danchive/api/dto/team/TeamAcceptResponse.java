package DumbAndDumber.Danchive.api.dto.team;

public class TeamAcceptResponse {
    private Long teamId;
    private String roleInTeam; // "member"
    public TeamAcceptResponse(Long teamId, String roleInTeam) {
        this.teamId = teamId; this.roleInTeam = roleInTeam;
    }
    public Long getTeamId() { return teamId; }
    public String getRoleInTeam() { return roleInTeam; }
}
