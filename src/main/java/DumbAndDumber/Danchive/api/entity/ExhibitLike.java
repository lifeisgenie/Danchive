package DumbAndDumber.Danchive.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="exhibit_likes", uniqueConstraints=@UniqueConstraint(columnNames={"exhibit_id","user_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExhibitLike {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="exhibit_id")
    private Exhibit exhibit;

    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="user_id")
    private User user;
}