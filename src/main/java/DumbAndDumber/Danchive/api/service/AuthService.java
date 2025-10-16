package DumbAndDumber.Danchive.api.service;

import DumbAndDumber.Danchive.api.dto.*;
import DumbAndDumber.Danchive.api.repository.UserRepository;
import DumbAndDumber.Danchive.api.util.CookieUtil;
import DumbAndDumber.Danchive.api.util.JwtUtil;
import DumbAndDumber.Danchive.api.util.PasswordPolicy;
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
import java.util.UUID;

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
        if (!PasswordPolicy.valid(req.getPassword())) {
            throw new IllegalArgumentException("비밀번호 규칙 불일치(8~16자, 영문/숫자/특수문자)");
        }

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

    public AuthResponse guestLogin(GuestLoginRequest req) {
        // QR payload 검증 로직은 이후 합의(지금은 존재/형식만 체크)
        if (req == null || req.getQr() == null || req.getQr().isBlank())
            throw new IllegalArgumentException("유효하지 않은 QR입니다.");

        String guestId = UUID.randomUUID().toString();
        // 게스트 AT: 6시간 유효(필요 시 환경변수화)
        String at = jwt.generateGuestAccessToken(guestId, 6 * 60);
        // 게스트는 DB 사용자 행이 없으므로 userDto는 최소 정보
        UserDto user = new UserDto(null, "guest@danchive", "Guest", "guest", "N/A");
        return new AuthResponse(at, user);
    }

    public Map<String, String> requestPasswordReset(PasswordResetRequestDto dto) {
        // 이메일 포맷만 검증. 존재하지 않아도 200으로 보안상 동일 응답
        if (dto == null || dto.getEmail() == null || dto.getEmail().isBlank())
            throw new IllegalArgumentException("이메일이 필요합니다.");

        // 존재하는 경우에만 토큰 생성 (외부로는 항상 ok 반환)
        userRepository.findByEmail(dto.getEmail()).ifPresent(u -> {
            String token = jwt.generatePasswordResetToken(u.getEmail(), 30); // 30분 유효
            // 초기에는 "메일 발송" 대신 토큰을 반환해 테스트 쉽게 진행
            // 실제 운영 시 메일/SMS 연동 후 이 반환은 제거
        });
        return Map.of("reset_token", jwt.generatePasswordResetToken(dto.getEmail(), 30));
    }

    public void confirmPasswordReset(PasswordResetConfirmDto dto) {
        if (dto == null || dto.getReset_token() == null || dto.getReset_token().isBlank())
            throw new IllegalArgumentException("reset_token이 필요합니다.");
        if (!PasswordPolicy.valid(dto.getNew_password()))
            throw new IllegalArgumentException("비밀번호 규칙 불일치(8~16자, 영문/숫자/특수문자)");

        if (!jwt.isResetToken(dto.getReset_token()))
            throw new IllegalArgumentException("유효하지 않은 토큰 타입");

        String email = jwt.getSubject(dto.getReset_token());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.setPassword(passwordEncoder.encode(dto.getNew_password()));
        // 기존 로그인(AT) 무효화: 강제 재로그인 유도
        user.setAccessToken(null);
        user.setAccessTokenExp(null);
    }
}
