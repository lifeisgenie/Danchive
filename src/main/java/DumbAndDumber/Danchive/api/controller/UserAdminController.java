package DumbAndDumber.Danchive.api.controller;

import DumbAndDumber.Danchive.api.dto.ApiResponse;
import DumbAndDumber.Danchive.api.dto.auth.CreateProfessorRequest;
import DumbAndDumber.Danchive.api.dto.user.UserDto;
import DumbAndDumber.Danchive.api.service.UserAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserAdminController {

    private final UserAdminService userAdminService;

    @PostMapping("/professors")
    public ResponseEntity<ApiResponse<UserDto>> createProfessor(@RequestBody CreateProfessorRequest req) {
        return ResponseEntity.status(201)
                .body(ApiResponse.success("교수 계정이 생성되었습니다.", userAdminService.createProfessor(req)));
    }
}