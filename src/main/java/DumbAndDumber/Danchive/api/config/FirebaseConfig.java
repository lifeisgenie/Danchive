package DumbAndDumber.Danchive.api.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    // 파일 시스템 경로나 비워둘 수 있게 default "" 설정
    @Value("${firebase.credentials.path:}")
    private String firebaseCredentialsPath;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        // 이미 초기화 되어 있으면 재사용
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        InputStream is = null;

        // 1) 우선 순위: 외부에서 주입된 파일 시스템 경로 (예: Jenkins, 운영 서버)
        if (firebaseCredentialsPath != null && !firebaseCredentialsPath.isBlank()) {
            is = new FileInputStream(firebaseCredentialsPath);
        } else {
            // 2) fallback: classpath 에 포함된 firebase JSON (로컬 개발용)
            ClassPathResource resource =
                    new ClassPathResource("firebase/danchive-firebase-adminsdk-fbsvc-0e59eb133e.json");
            if (resource.exists()) {
                is = resource.getInputStream();
            }
        }

        if (is == null) {
            // 여기서 바로 죽여버리면, 설정 꼬인 걸 빨리 발견할 수 있음
            throw new IllegalStateException(
                    "Firebase credentials not found. " +
                    "Set 'firebase.credentials.path' or put the JSON under classpath:firebase/...");
        }

        GoogleCredentials credentials = GoogleCredentials.fromStream(is);

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();

        return FirebaseApp.initializeApp(options);
    }
}