package DumbAndDumber.Danchive.api.controller;

import DumbAndDumber.Danchive.api.dto.ApiResponse;
import DumbAndDumber.Danchive.api.dto.FcmTokenRequest;
import DumbAndDumber.Danchive.api.service.UserNotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final UserNotificationService userNotificationService;

    @PostMapping("/fcm-token")
    public ApiResponse<Void> registerFcmToken(@RequestBody @Valid FcmTokenRequest req) {
        userNotificationService.updateMyFcmToken(req.getToken());
        return ApiResponse.success("FCM 토큰이 등록되었습니다.");
    }

    @DeleteMapping("/fcm-token")
    public ApiResponse<Void> deleteFcmToken() {
        userNotificationService.clearMyFcmToken();
        return ApiResponse.success("FCM 토큰이 삭제되었습니다.");
    }
}