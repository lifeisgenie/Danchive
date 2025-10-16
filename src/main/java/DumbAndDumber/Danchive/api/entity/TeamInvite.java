package DumbAndDumber.Danchive.api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "team_invites")
public class TeamInvite {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    @JoinColumn(name="team_id")
    private Team team;

    @Column(nullable=false, length=255)
    private String email;

    @Column(nullable=false)
    private Long invitedByUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=16)
    private InviteStatus status = InviteStatus.PENDING;

    @Column(nullable=false, updatable=false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime expiresAt; // 선택

    protected TeamInvite() {}
    public TeamInvite(Team team, String email, Long invitedByUserId) {
        this.team = team; this.email = email; this.invitedByUserId = invitedByUserId;
    }

    public Long getId() { return id; }
    public Team getTeam() { return team; }
    public String getEmail() { return email; }
    public Long getInvitedByUserId() { return invitedByUserId; }
    public InviteStatus getStatus() { return status; }
    public void accept() { this.status = InviteStatus.ACCEPTED; }
    public void cancel() { this.status = InviteStatus.CANCELED; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}
