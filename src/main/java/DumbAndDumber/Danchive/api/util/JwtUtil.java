package DumbAndDumber.Danchive.api.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private final Key key;
    private final long accessExpMinutes;
    private final long refreshExpDays;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.access-exp-minutes}") long accessExpMinutes,
                   @Value("${jwt.refresh-exp-days}") long refreshExpDays) {
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
    public Jws<Claims> parse(String token) { return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token); }
    public String getSubject(String token) { return parse(token).getBody().getSubject(); }
    public Instant getExpiration(String token){ return parse(token).getBody().getExpiration().toInstant(); }

    public String generatePasswordResetToken(String email, long expMinutes) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(email)
                .claim("typ", "reset")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(expMinutes, ChronoUnit.MINUTES)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    public boolean isResetToken(String token) {
        return "reset".equals(parse(token).getBody().get("typ", String.class));
    }
    /** 게스트 subject = guest:<uuid> */
    public String generateGuestAccessToken(String guestId, long expMinutes) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject("guest:" + guestId)
                .claim("role", "guest")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(expMinutes, ChronoUnit.MINUTES)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}