package DumbAndDumber.Danchive.api.service;

import DumbAndDumber.Danchive.api.dto.auth.*;
import DumbAndDumber.Danchive.api.dto.user.UserDto;
import DumbAndDumber.Danchive.api.entity.User;
import DumbAndDumber.Danchive.api.repository.UserRepository;
import DumbAndDumber.Danchive.api.util.CookieUtil;
import DumbAndDumber.Danchive.api.util.JwtUtil;
import DumbAndDumber.Danchive.api.util.PasswordPolicy;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwt;
    private final PasswordEncoder passwordEncoder;

    private static final int RT_MAX_AGE_SEC = 7 * 24 * 60 * 60;
    private static final String COOKIE_DOMAIN = "";

    public UserDto register(RegisterRequest req) {
        if (!PasswordPolicy.valid(req.getPassword()))
            throw new IllegalArgumentException("비밀번호 규칙 불일치(8~16자, 영문/숫자/특수문자)");

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

    public LoginResponse login(LoginRequest req, HttpServletResponse res) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일을 찾을 수 없습니다."));
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword()))
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");

        Map<String,Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        claims.put("name", user.getName());

        String accessToken = jwt.generateAccessToken(user.getEmail(), claims);
        String refreshToken = jwt.generateRefreshToken(user.getEmail());

        user.setAccessToken(accessToken);
        user.setAccessTokenExp(jwt.getExpiration(accessToken));
        CookieUtil.addHttpOnlyCookie(res, CookieUtil.RT_COOKIE, refreshToken, RT_MAX_AGE_SEC, COOKIE_DOMAIN);

        return new LoginResponse(accessToken, UserDto.of(user));
    }

    public LoginResponse refresh(HttpServletRequest req, HttpServletResponse res) {
        String rt = CookieUtil.getCookie(req, CookieUtil.RT_COOKIE)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token이 필요합니다."));
        String email = jwt.getSubject(rt);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Map<String,Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        claims.put("name", user.getName());

        String newAT = jwt.generateAccessToken(user.getEmail(), claims);
        user.setAccessToken(newAT);
        user.setAccessTokenExp(jwt.getExpiration(newAT));

        // 운영 시 RT 회전 권장:
        // String newRT = jwt.generateRefreshToken(user.getEmail());
        // CookieUtil.addHttpOnlyCookie(res, CookieUtil.RT_COOKIE, newRT, RT_MAX_AGE_SEC, COOKIE_DOMAIN);

        return new LoginResponse(newAT, UserDto.of(user));
    }

    public void logout(HttpServletRequest req, HttpServletResponse res, String bearerAT) {
        String at = bearerAT != null && bearerAT.startsWith("Bearer ") ? bearerAT.substring(7) : null;
        if (at != null) {
            userRepository.findByAccessToken(at).ifPresent(u -> {
                u.setAccessToken(null);
                u.setAccessTokenExp(null);
            });
        }
        CookieUtil.clearCookie(res, CookieUtil.RT_COOKIE, COOKIE_DOMAIN);
    }

    public LoginResponse guestLogin(GuestLoginRequest req) {
        if (req == null || req.getQr() == null || req.getQr().isBlank())
            throw new IllegalArgumentException("유효하지 않은 QR입니다.");
        String guestId = UUID.randomUUID().toString();
        String at = jwt.generateGuestAccessToken(guestId, 6 * 60);
        UserDto user = new UserDto(null, "guest@danchive", "Guest", "guest", "N/A");
        return new LoginResponse(at, user);
    }

    public Map<String, String> requestPasswordReset(PasswordResetRequestDto dto) {
        if (dto == null || dto.getEmail() == null || dto.getEmail().isBlank())
            throw new IllegalArgumentException("이메일이 필요합니다.");
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
        user.setAccessToken(null);
        user.setAccessTokenExp(null);
    }
}