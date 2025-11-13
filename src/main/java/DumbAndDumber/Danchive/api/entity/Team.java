package DumbAndDumber.Danchive.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="teams")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Team {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=100)
    private String name;

    public Team(String name){ this.name = name; }
}