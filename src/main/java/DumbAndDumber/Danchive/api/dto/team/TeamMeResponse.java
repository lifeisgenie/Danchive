package DumbAndDumber.Danchive.api.dto.team;

import java.util.List;

public class TeamMeResponse {
    private Long teamId;
    private String teamName;
    private List<TeamMemberDto> members;

    public TeamMeResponse(Long teamId, String teamName, List<TeamMemberDto> members) {
        this.teamId = teamId; this.teamName = teamName; this.members = members;
    }
    public Long getTeamId() { return teamId; }
    public String getTeamName() { return teamName; }
    public List<TeamMemberDto> getMembers() { return members; }
}
