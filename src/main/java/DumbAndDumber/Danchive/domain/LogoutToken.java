package DumbAndDumber.Danchive.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Entity
@Getter
@NoArgsConstructor
public class LogoutToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private Instant expiredAt; // 언제까지 무효화 유지할지

    public LogoutToken(String token, Instant expiredAt) {
        this.token = token;
        this.expiredAt = expiredAt;
    }
}
