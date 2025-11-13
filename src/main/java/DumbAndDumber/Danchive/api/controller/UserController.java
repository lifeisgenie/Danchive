package DumbAndDumber.Danchive.api.controller;

import DumbAndDumber.Danchive.api.dto.ApiResponse;
import DumbAndDumber.Danchive.api.dto.user.UserDto;
import DumbAndDumber.Danchive.api.util.SecurityUtil;
import DumbAndDumber.Danchive.api.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> me() {
        User u = SecurityUtil.getCurrentUserOrThrow();
        return ResponseEntity.ok(ApiResponse.success("내 정보 조회 성공",
                new UserDto(u.getId(), u.getEmail(), u.getName(), u.getRole(), u.getDepartment())));
    }

    // PATCH /users/me, DELETE /users/me 는 이후 단계에서 붙이면 됨
}