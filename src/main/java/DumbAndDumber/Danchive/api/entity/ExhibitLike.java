package DumbAndDumber.Danchive.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Table(name="exhibit_likes", uniqueConstraints = {
        @UniqueConstraint(name="uk_exhibit_like_user", columnNames = {"exhibit_id", "user_id"})
})
public class ExhibitLike {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    private Exhibit exhibit;

    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    private User user;
}
