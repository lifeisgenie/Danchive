package DumbAndDumber.Danchive.api.service.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    // 반환값: 접근가능한 URL
    String upload(String path, MultipartFile file);
}
