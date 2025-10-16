package DumbAndDumber.Danchive.api.service;

import DumbAndDumber.Danchive.api.dto.AuthResponse;
import DumbAndDumber.Danchive.api.dto.LoginRequest;
import DumbAndDumber.Danchive.api.dto.RegisterRequest;
import DumbAndDumber.Danchive.api.dto.UserDto;
import DumbAndDumber.Danchive.api.repository.UserRepository;
import DumbAndDumber.Danchive.api.util.CookieUtil;
import DumbAndDumber.Danchive.api.util.JwtUtil;
import DumbAndDumber.Danchive.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwt;
    private final PasswordEncoder passwordEncoder;

    // 애플리케이션 속성에서 주입해도 되지만, 단순화를 위해 하드코딩/상수화
    private static final int RT_MAX_AGE_SEC = 7 * 24 * 60 * 60; // refresh-exp-days와 일치
    private static final String COOKIE_DOMAIN = ""; // prod 도메인 있으면 지정(e.g. ".danchive.com")

    public UserDto register(RegisterRequest req) {
        userRepository.findByEmail(req.getEmail())
                .ifPresent(u -> { throw new IllegalArgumentException("이미 존재하는 이메일입니다."); });

        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .name(req.getName())
                .role(req.getRole())
                .department(req.getDepartment())
                .build();

        userRepository.save(user);
        return UserDto.of(user);
    }

    public AuthResponse login(LoginRequest req, HttpServletResponse res) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일을 찾을 수 없습니다."));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        // AT/RT 발급
        Map<String,Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        claims.put("name", user.getName());

        String accessToken = jwt.generateAccessToken(user.getEmail(), claims);
        String refreshToken = jwt.generateRefreshToken(user.getEmail());

        // DB 화이트리스트 저장(만료도 같이)
        user.setAccessToken(accessToken);
        user.setAccessTokenExp(jwt.getExpiration(accessToken));

        // RT는 HttpOnly 쿠키
        CookieUtil.addHttpOnlyCookie(res, CookieUtil.RT_COOKIE, refreshToken, RT_MAX_AGE_SEC, COOKIE_DOMAIN);

        return new AuthResponse(accessToken, UserDto.of(user));
    }

    public AuthResponse refresh(HttpServletRequest req, HttpServletResponse res) {
        String rt = CookieUtil.getCookie(req, CookieUtil.RT_COOKIE)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token이 필요합니다."));

        String email = jwt.getSubject(rt);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // RT가 유효하면 새로운 AT 발급 (RT 회전이 필요하면 여기서 새 RT도 발급/쿠키 교체)
        Map<String,Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        claims.put("name", user.getName());

        String newAT = jwt.generateAccessToken(user.getEmail(), claims);
        user.setAccessToken(newAT);
        user.setAccessTokenExp(jwt.getExpiration(newAT));

        // (선택) RT 회전: 보안 강화 필요 시 아래 주석 해제
        // String newRT = jwt.generateRefreshToken(user.getEmail());
        // CookieUtil.addHttpOnlyCookie(res, CookieUtil.RT_COOKIE, newRT, RT_MAX_AGE_SEC, COOKIE_DOMAIN);

        return new AuthResponse(newAT, UserDto.of(user));
    }

    public void logout(HttpServletRequest req, HttpServletResponse res, String bearerAT) {
        // 화이트리스트에서 AT 제거(해당 디바이스에서만 로그아웃하려면 email/AT 매칭으로 처리)
        String at = bearerAT != null && bearerAT.startsWith("Bearer ") ? bearerAT.substring(7) : null;
        if (at != null) {
            userRepository.findByAccessToken(at).ifPresent(u -> {
                u.setAccessToken(null);
                u.setAccessTokenExp(null);
            });
        }
        CookieUtil.clearCookie(res, CookieUtil.RT_COOKIE, COOKIE_DOMAIN);
    }
}
