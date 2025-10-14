package DumbAndDumber.Danchive.api.controller;

import DumbAndDumber.Danchive.api.dto.LoginRequest;
import DumbAndDumber.Danchive.api.dto.LoginResponse;
import DumbAndDumber.Danchive.api.dto.RegisterRequest;
import DumbAndDumber.Danchive.api.service.AuthService;
import DumbAndDumber.Danchive.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User newUser = authService.register(request);
            return ResponseEntity.status(201).body(newUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    // Access Token 재발급
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body("Refresh token이 필요합니다.");
            }

            String refreshToken = authHeader.substring(7);
            LoginResponse response = authService.refresh(refreshToken);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Access token이 필요합니다.");
        }

        // 실제 로그아웃 로직은 나중에 JWT 블랙리스트(DB) 추가 시 구현
        return ResponseEntity.ok("로그아웃 완료 (임시 응답)");
    }
}
