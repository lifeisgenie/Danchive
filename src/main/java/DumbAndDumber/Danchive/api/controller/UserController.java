package DumbAndDumber.Danchive.api.controller;

import DumbAndDumber.Danchive.api.repository.UserRepository;
import DumbAndDumber.Danchive.api.util.JwtUtil;
import DumbAndDumber.Danchive.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @GetMapping("/me")
    public User getMyInfo(@RequestHeader("Authorization") String bearerToken) {
        try {
            // Authorization 헤더에서 토큰 추출
            String token = bearerToken.replace("Bearer ", "");

            // JWT 안에 들어있는 subject(email) 꺼내기
            String email = jwtUtil.getSubject(token);

            // DB에서 유저 조회
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isEmpty()) {
                throw new RuntimeException("User not found");
            }

            return optionalUser.get();

        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired token");
        }
    }
}
