package DumbAndDumber.Danchive.api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "teams")
public class Team {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique = true, length = 100)
    private String name;

    @Column(nullable=false, updatable=false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected Team() {}
    public Team(String name) { this.name = name; }

    public Long getId() { return id; }
    public String getName() { return name; }
}
