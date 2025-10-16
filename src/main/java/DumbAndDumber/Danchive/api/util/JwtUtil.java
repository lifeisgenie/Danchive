package DumbAndDumber.Danchive.api.util;

import io.jsonwebtoken.*;                   // JWT 생성, 검증용 라이브러리
import io.jsonwebtoken.security.Keys;       // Secret 키 생성 유틸
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;   // 무자열 -> 바이트 변환 시 인코딩
import java.security.Key;                   // 암호화 키 객체
import java.time.Instant;                   // 현재 시간용
import java.time.temporal.ChronoUnit;       // 시간 단위 (분, 일 등)
import java.util.Date;
import java.util.Map;

/**
 * JWT 토큰 생성 및 검증을 담당하는 유틸리티 클래스.
 * - AccessToken, RefreshToken 발급
 * - 토큰 파싱 및 Subject(이메일 등 식별자) 추출
 */
@Component
public class JwtUtil {

    private final Key key;
    private final long accessExpMinutes;
    private final long refreshExpDays;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-exp-minutes}") long accessExpMinutes,
            @Value("${jwt.refresh-exp-days}") long refreshExpDays
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpMinutes = accessExpMinutes;
        this.refreshExpDays = refreshExpDays;
    }

    public String generateAccessToken(String subject, Map<String,Object> claims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(accessExpMinutes, ChronoUnit.MINUTES)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String subject) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(refreshExpDays, ChronoUnit.DAYS)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    public String getSubject(String token) { return parse(token).getBody().getSubject(); }

    public Instant getExpiration(String token) {
        return parse(token).getBody().getExpiration().toInstant();
    }
}