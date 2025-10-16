package DumbAndDumber.Danchive.api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "team_members",
        uniqueConstraints = {
                @UniqueConstraint(name="uk_team_user", columnNames = {"team_id", "user_id"}),
                @UniqueConstraint(name="uk_user_single_team", columnNames = {"user_id"})
        })
public class TeamMember {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="team_id")
    private Team team;

    @Column(name="user_id", nullable=false)
    private Long userId; // 기존 User 엔티티의 PK (연관관계 단순화)

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=10)
    private TeamRole role;

    protected TeamMember() {}
    public TeamMember(Team team, Long userId, TeamRole role) {
        this.team = team; this.userId = userId; this.role = role;
    }
    public Long getUserId() { return userId; }
    public TeamRole getRole() { return role; }
    public Team getTeam() { return team; }
}
