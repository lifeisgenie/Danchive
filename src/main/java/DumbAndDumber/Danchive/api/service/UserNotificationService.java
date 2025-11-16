package DumbAndDumber.Danchive.api.service;

import DumbAndDumber.Danchive.api.entity.User;
import DumbAndDumber.Danchive.api.repository.UserRepository;
import DumbAndDumber.Danchive.api.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserNotificationService {

    private final UserRepository userRepository;

    public void updateMyFcmToken(String token) {
        User me = SecurityUtil.getCurrentUserOrThrow();
        me.setFcmToken(token);
    }

    public void clearMyFcmToken() {
        User me = SecurityUtil.getCurrentUserOrThrow();
        me.setFcmToken(null);
    }
}