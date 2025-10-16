package DumbAndDumber.Danchive.api.dto.team;

import java.time.LocalDateTime;

public class TeamInviteSummaryDto {
    private Long inviteId;
    private Long teamId;
    private String teamName;
    private String status;        // "pending" | "accepted" | ...
    private LocalDateTime invitedAt;

    public TeamInviteSummaryDto(Long inviteId, Long teamId, String teamName, String status, LocalDateTime invitedAt) {
        this.inviteId = inviteId;
        this.teamId = teamId;
        this.teamName = teamName;
        this.status = status;
        this.invitedAt = invitedAt;
    }
    public Long getInviteId() { return inviteId; }
    public Long getTeamId() { return teamId; }
    public String getTeamName() { return teamName; }
    public String getStatus() { return status; }
    public LocalDateTime getInvitedAt() { return invitedAt; }
}
