package DumbAndDumber.Danchive.api.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=190)
    private String email;

    @JsonIgnore
    @Column(nullable=false)
    private String password;

    @Column(nullable=false, length=80)
    private String name;

    @Column(nullable=false, length=32)
    private String role;          // e.g. "team", "admin"

    @Column(nullable=false, length=32)
    private String department;    // e.g. "SW"

    /** 화이트리스트 방식: 현재 유효한 AccessToken(마지막 로그인/재발급) */
    @JsonIgnore
    @Column(name="access_token", length=512)
    private String accessToken;

    /** AT 만료 시각(Unix epoch seconds) */
    @JsonIgnore
    @Column(name="access_token_exp")
    private Instant accessTokenExp;
}
