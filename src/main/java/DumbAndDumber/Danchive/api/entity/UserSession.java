package DumbAndDumber.Danchive.api.entity;

import DumbAndDumber.Danchive.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_sessions")
@Getter @Setter
public class UserSession {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 한 유저가 여러 기기에서 로그인 가능
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 500)
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime expiresAt;
}
