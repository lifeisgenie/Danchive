package DumbAndDumber.Danchive.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="team_memberships",
    uniqueConstraints=@UniqueConstraint(columnNames={"team_id","user_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TeamMembership {

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false) @JoinColumn(name="team_id")
    private Team team;

    @ManyToOne(optional=false) @JoinColumn(name="user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=10)
    private TeamRole role;

    public TeamMembership(Team t, User u, TeamRole r){ this.team=t; this.user=u; this.role=r; }
}