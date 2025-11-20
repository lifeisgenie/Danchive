package DumbAndDumber.Danchive.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity @Table(name="users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=190)
    private String email;

    @JsonIgnore
    @Column(nullable=false)
    private String password;

    @Column(nullable=false, length=80)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private Role role;

    @Column(length=50)
    private String department;

    @Column(length = 20)
    private String studentId;

    // AccessToken 화이트리스트
    @JsonIgnore
    @Column(length=2048)
    private String accessToken;

    @JsonIgnore
    private Instant accessTokenExp;

    @Column(length = 255)
    private String fcmToken; // 한 유저당 한 기기
}