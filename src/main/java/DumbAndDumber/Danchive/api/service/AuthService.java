package DumbAndDumber.Danchive.api.service;

import DumbAndDumber.Danchive.api.dto.LoginRequest;
import DumbAndDumber.Danchive.api.dto.LoginResponse;
import DumbAndDumber.Danchive.api.dto.RegisterRequest;
import DumbAndDumber.Danchive.api.repository.UserRepository;
import DumbAndDumber.Danchive.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    // 회원가입
    public User register(RegisterRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setName(request.getName());
        user.setRole(request.getRole());
        user.setDepartment(request.getDepartment());
        return userRepository.save(user);
    }

    // 로그인
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 임시 토큰 (나중에 JWT로 교체 예정)
        String fakeAccessToken = "ACCESS_TOKEN_" + user.getId();
        String fakeRefreshToken = "REFRESH_TOKEN_" + user.getId();

        return new LoginResponse(fakeAccessToken, fakeRefreshToken, user);
    }
}
