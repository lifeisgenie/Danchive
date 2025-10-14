package DumbAndDumber.Danchive.api.service;

import DumbAndDumber.Danchive.api.dto.LoginRequest;
import DumbAndDumber.Danchive.api.dto.LoginResponse;
import DumbAndDumber.Danchive.api.dto.RegisterRequest;
import DumbAndDumber.Danchive.api.repository.UserRepository;
import DumbAndDumber.Danchive.api.util.JwtUtil;
import DumbAndDumber.Danchive.domain.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입
    public User register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // 암호화 저장
        user.setName(request.getName());
        user.setRole(request.getRole());
        user.setDepartment(request.getDepartment());

        return userRepository.save(user);
    }

    // 로그인
    public LoginResponse login(LoginRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("이메일을 찾을 수 없습니다.");
        }

        User user = optionalUser.get();

        // 암호화된 비밀번호 비교
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 올바르지 않습니다.");
        }

        // JWT 발급
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        claims.put("name", user.getName());

        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), claims);
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        return new LoginResponse(accessToken, refreshToken, user);
    }

    // Refresh Token으로 Access Token 재발급
    public LoginResponse refresh(String refreshToken) {
        try {
            String email = jwtUtil.getSubject(refreshToken);

            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isEmpty()) {
                throw new RuntimeException("User not found");
            }

            User user = optionalUser.get();

            Map<String, Object> claims = new HashMap<>();
            claims.put("role", user.getRole());
            claims.put("name", user.getName());

            String newAccessToken = jwtUtil.generateAccessToken(user.getEmail(), claims);

            return new LoginResponse(newAccessToken, refreshToken, user);

        } catch (Exception e) {
            throw new RuntimeException("Invalid refresh token");
        }
    }
}
