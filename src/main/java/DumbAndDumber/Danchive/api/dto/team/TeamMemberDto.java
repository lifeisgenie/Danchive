package DumbAndDumber.Danchive.api.dto.team;

public class TeamMemberDto {
    private Long userId;
    private String name;
    private String roleInTeam; // "leader"/"member"

    public TeamMemberDto(Long userId, String name, String roleInTeam) {
        this.userId = userId; this.name = name; this.roleInTeam = roleInTeam;
    }
    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public String getRoleInTeam() { return roleInTeam; }
}
