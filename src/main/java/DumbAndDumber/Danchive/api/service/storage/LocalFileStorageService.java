package DumbAndDumber.Danchive.api.service.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    /**
     * 저장 루트 디렉토리 (예: ./uploads)
     * application.yml에서 file.storage.root 로 설정 가능
     */
    @Value("${file.storage.root:./uploads}")
    private String rootDir;

    /**
     * 공개 접근용 베이스 URL (예: /files/)
     * application.yml에서 file.storage.public-base-url 로 설정
     */
    @Value("${file.storage.public-base-url:/files/}")
    private String publicBaseUrl;

    @Override
    public String upload(String path, MultipartFile file) {
        try {
            // 저장 경로 생성
            String original = file.getOriginalFilename();
            String ext = "";
            if (original != null && original.lastIndexOf('.') != -1) {
                ext = original.substring(original.lastIndexOf('.')); // ".jpg"
            }
            String filename = UUID.randomUUID() + ext;

            Path dir = Path.of(rootDir, path).normalize();
            Files.createDirectories(dir);
            Path dest = dir.resolve(filename).normalize();

            file.transferTo(dest.toFile());

            // 공개 URL 반환 (/files/<path>/<filename>)
            String normalizedPath = path.startsWith("/") ? path.substring(1) : path;
            if (!normalizedPath.endsWith("/")) normalizedPath += "/";
            return publicBaseUrl + normalizedPath + filename;
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패: " + e.getMessage(), e);
        }
    }
}
