package DumbAndDumber.Danchive.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exhibit_guest_likes",
       uniqueConstraints = @UniqueConstraint(columnNames = {"gid", "exhibit_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExhibitGuestLike {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=64)
    private String gid; // guest uuid

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exhibit_id")
    private Exhibit exhibit;
}
