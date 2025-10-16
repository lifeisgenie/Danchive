package DumbAndDumber.Danchive.api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "team_memberships",
        uniqueConstraints = {
                @UniqueConstraint(name="uq_team_user", columnNames = {"team_id", "user_id"}),
                @UniqueConstraint(name="uq_user_single_team", columnNames = {"user_id"})
        })
public class TeamMembership {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    @JoinColumn(name="team_id")
    private Team team;

    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=16)
    private TeamRole role; // LEADER / MEMBER

    protected TeamMembership() {}
    public TeamMembership(Team team, User user, TeamRole role) {
        this.team = team; this.user = user; this.role = role;
    }

    public Long getId() { return id; }
    public Team getTeam() { return team; }
    public User getUser() { return user; }
    public TeamRole getRole() { return role; }
}
