package DumbAndDumber.Danchive.api.controller;

import DumbAndDumber.Danchive.api.dto.RegisterRequest;
import DumbAndDumber.Danchive.api.dto.LoginRequest;
import DumbAndDumber.Danchive.api.dto.LoginResponse;
import DumbAndDumber.Danchive.api.service.AuthService;
import DumbAndDumber.Danchive.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // 회원가입
    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    // 로그인
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
