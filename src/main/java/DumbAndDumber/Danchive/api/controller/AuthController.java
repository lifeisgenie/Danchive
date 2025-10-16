package DumbAndDumber.Danchive.api.controller;

import DumbAndDumber.Danchive.api.dto.AuthResponse;
import DumbAndDumber.Danchive.api.dto.LoginRequest;
import DumbAndDumber.Danchive.api.dto.RegisterRequest;
import DumbAndDumber.Danchive.api.dto.UserDto;
import DumbAndDumber.Danchive.api.service.AuthService;
import DumbAndDumber.Danchive.domain.User;
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
    public ResponseEntity<UserDto> register(@RequestBody RegisterRequest req) {
        return ResponseEntity.status(201).body(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req, HttpServletResponse res) {
        return ResponseEntity.ok(authService.login(req, res));
    }

    /** RT는 HttpOnly 쿠키(RT)로만 받음 */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(HttpServletRequest req, HttpServletResponse res) {
        return ResponseEntity.ok(authService.refresh(req, res));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest req,
            HttpServletResponse res,
            @RequestHeader(value="Authorization", required=false) String bearer
    ) {
        authService.logout(req, res, bearer);
        return ResponseEntity.ok().build();
    }
}