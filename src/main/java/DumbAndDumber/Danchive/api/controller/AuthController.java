package DumbAndDumber.Danchive.api.controller;

import DumbAndDumber.Danchive.api.dto.*;
import DumbAndDumber.Danchive.api.dto.auth.*;
import DumbAndDumber.Danchive.api.dto.user.UserDto;
import DumbAndDumber.Danchive.api.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> register(@RequestBody RegisterRequest req) {
        UserDto user = authService.register(req);
        return ResponseEntity.status(201)
                .body(ApiResponse.success("회원가입이 완료되었습니다.", user));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody LoginRequest req,
            HttpServletResponse res) {

        LoginResponse response = authService.login(req, res);
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", response));
    }

    /** RT는 HttpOnly 쿠키에서 읽음(바디/헤더 불필요) */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(
            HttpServletRequest req, HttpServletResponse res) {

        LoginResponse response = authService.refresh(req, res);
        return ResponseEntity.ok(ApiResponse.success("Access Token 재발급 완료", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest req,
            HttpServletResponse res,
            @RequestHeader(value="Authorization", required=false) String bearer) {

        authService.logout(req, res, bearer);
        return ResponseEntity.ok(ApiResponse.success("로그아웃이 완료되었습니다."));
    }

    /** 게스트(비회원) 로그인 - QR 진입 */
    @PostMapping("/guest")
    public ResponseEntity<ApiResponse<LoginResponse>> guest(@RequestBody GuestLoginRequest req) {
        LoginResponse response = authService.guestLogin(req);
        return ResponseEntity.ok(ApiResponse.success("비회원(게스트) 로그인 성공", response));
    }

    /** 비밀번호 재설정(요청) - 초기에는 reset_token을 응답으로 돌려줌 */
    @PostMapping("/password/reset/request")
    public ResponseEntity<ApiResponse<?>> passwordResetRequest(
            @RequestBody PasswordResetRequestDto dto) {

        var tokenMap = authService.requestPasswordReset(dto);
        return ResponseEntity.ok(ApiResponse.success("비밀번호 재설정 토큰이 발급되었습니다.", tokenMap));
    }

    /** 비밀번호 재설정(확정) */
    @PostMapping("/password/reset/confirm")
    public ResponseEntity<ApiResponse<Void>> passwordResetConfirm(
            @RequestBody PasswordResetConfirmDto dto) {

        authService.confirmPasswordReset(dto);
        return ResponseEntity.ok(ApiResponse.success("비밀번호가 재설정되었습니다."));
    }
}