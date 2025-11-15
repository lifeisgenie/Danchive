package DumbAndDumber.Danchive.api.service.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String upload(String path, MultipartFile file); // 반환: 접근 가능한 URL
}
