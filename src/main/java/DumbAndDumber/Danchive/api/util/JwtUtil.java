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

    // JWT 서명용 비밀키
    private final Key key;

    // 액세스 토큰 만료 시간(분 단위)
    private final long accessExpMinutes;

    // 리프레시 토큰 만료 시간(일 단위)
    private final long refreshExpDays;


    /**
     * application.properties에 설정된 값을 불러와 주입
     *
     * @param secret JWT 서명용 비밀키 (Base64 or 문자열)
     * @param accessExpMinutes AccessToken 만료 시간(분)
     * @param refreshExpDays RefreshToken 만료 시간(일)
     */
    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-exp-minutes}") long accessExpMinutes,
            @Value("${jwt.refresh-exp-days}") long refreshExpDays
    ) {
        // 문자열로 입력된 secret을 HMAC 서명용 키 객체로 변환
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpMinutes = accessExpMinutes;
        this.refreshExpDays = refreshExpDays;
    }

    /**
     * Access Token 생성 메서드
     *
     * @param subject  JWT의 주제(대개 이메일, 사용자 ID 등)
     * @param claims   JWT에 포함할 추가 데이터(Map 형태)
     * @return         서명된 JWT 문자열
     */
    public String generateAccessToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(accessExpMinutes, ChronoUnit.MINUTES)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Refresh Token 생성 메서드
     *
     * @param subject  주제(이메일 등)
     * @return         RefreshToken 문자열
     */
    public String generateRefreshToken(String subject) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(refreshExpDays, ChronoUnit.DAYS)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT 파싱(서명 검증 + 내용 추출)
     * - 유효하지 않거나 위조된 토큰이면 예외 발생
     *
     * @param token JWT 문자열 (Bearer 제거된 상태)
     * @return      파싱된 Jws<Claims> 객체
     */
    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    /**
     * 토큰에서 subject(사용자 이메일 등) 추출
     *
     * @param token JWT 문자열
     * @return      subject 값
     */
    public String getSubject(String token) {
        return parse(token).getBody().getSubject();
    }
}
