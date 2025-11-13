package DumbAndDumber.Danchive.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity @Table(name="team_invites")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TeamInvite {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false) @JoinColumn(name="team_id")
    private Team team;

    @Column(nullable=false, length=190)
    private String email;

    @Column(nullable=false)
    private Long invitedByUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=10)
    private InviteStatus status = InviteStatus.PENDING;

    @Column(nullable=false)
    private Instant createdAt = Instant.now();

    public TeamInvite(Team team, String email, Long inviter){
        this.team = team; this.email=email; this.invitedByUserId = inviter;
    }
    public void accept(){ this.status = InviteStatus.ACCEPTED; }
}