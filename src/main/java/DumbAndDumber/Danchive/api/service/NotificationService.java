package DumbAndDumber.Danchive.api.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final FirebaseApp firebaseApp;

    public void sendNoticeToTokens(List<String> tokens, String title, String body, Map<String, String> data) {
        List<String> validTokens = tokens.stream()
                .filter(t -> t != null && !t.isBlank())
                .toList();

        if (validTokens.isEmpty()) return;

        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(validTokens)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putAllData(data != null ? data : Map.of())
                .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance(firebaseApp).sendMulticast(message);
            log.info("FCM multicast: success={}, failure={}",
                    response.getSuccessCount(), response.getFailureCount());
        } catch (Exception e) {
            log.warn("Failed to send FCM multicast: {}", e.getMessage());
        }
    }
}