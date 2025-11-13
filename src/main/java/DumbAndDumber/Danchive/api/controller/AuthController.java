package DumbAndDumber.Danchive.api.controller;

import DumbAndDumber.Danchive.api.dto.*;
import DumbAndDumber.Danchive.api.dto.auth.*;
import DumbAndDumber.Danchive.api.dto.user.UserDto;
import DumbAndDumber.Danchive.api.service.AuthService;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/auth") @RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> register(@RequestBody RegisterRequest req) {
        return ResponseEntity.status(201).body(ApiResponse.success("회원가입이 완료되었습니다.", authService.register(req)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest req, HttpServletResponse res) {
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", authService.login(req, res)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(HttpServletRequest req, HttpServletResponse res) {
        return ResponseEntity.ok(ApiResponse.success("Access Token 재발급 완료", authService.refresh(req, res)));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest req, HttpServletResponse res,
                                       @RequestHeader(value="Authorization", required=false) String bearer) {
        authService.logout(req, res, bearer);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/guest")
    public ResponseEntity<ApiResponse<LoginResponse>> guest(@RequestBody GuestLoginRequest req) {
        return ResponseEntity.ok(ApiResponse.success("비회원(게스트) 로그인 성공", authService.guestLogin(req)));
    }

    @PostMapping("/password/reset/request")
    public ResponseEntity<ApiResponse<?>> passwordResetRequest(@RequestBody PasswordResetRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success("비밀번호 재설정 토큰이 발급되었습니다.", authService.requestPasswordReset(dto)));
    }

    @PostMapping("/password/reset/confirm")
    public ResponseEntity<ApiResponse<Void>> passwordResetConfirm(@RequestBody PasswordResetConfirmDto dto) {
        authService.confirmPasswordReset(dto);
        return ResponseEntity.ok(ApiResponse.success("비밀번호가 재설정되었습니다."));
    }
}