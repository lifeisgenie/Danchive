package DumbAndDumber.Danchive.api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="team_invites")
public class TeamInvite {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="team_id")
    private Team team;

    @Column(nullable=false, length=150)
    private String email;

    @Column(nullable=false)
    private Long inviterUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=10)
    private InviteStatus status = InviteStatus.PENDING;

    @Column(nullable=false, updatable=false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime expiresAt; // 필요 시 만료 정책 (예: 7일)

    protected TeamInvite() {}
    public TeamInvite(Team team, String email, Long inviterUserId, LocalDateTime expiresAt) {
        this.team = team; this.email = email; this.inviterUserId = inviterUserId; this.expiresAt = expiresAt;
    }

    public Long getId() { return id; }
    public Team getTeam() { return team; }
    public String getEmail() { return email; }
    public InviteStatus getStatus() { return status; }
    public void accept() { this.status = InviteStatus.ACCEPTED; }
    public boolean isExpired() { return expiresAt != null && LocalDateTime.now().isAfter(expiresAt); }
    public void cancel() { this.status = InviteStatus.CANCELED; }
}
