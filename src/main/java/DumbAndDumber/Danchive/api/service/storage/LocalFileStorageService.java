package DumbAndDumber.Danchive.api.service.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    private final Path rootLocation;

    // app.upload-dir 없으면 기본값 uploads 사용
    public LocalFileStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            // 루트 디렉토리만 미리 생성
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("업로드 디렉토리 생성 실패: " + rootLocation, e);
        }
    }

    @Override
    public String upload(String basePath, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        // basePath 정리 (앞/뒤 슬래시 정규화)
        String cleanedBasePath = normalizeBasePath(basePath);

        String originalName = StringUtils.cleanPath(file.getOriginalFilename());
        String ext = getExtension(originalName);
        String storedName = UUID.randomUUID() + (ext != null ? "." + ext : "");

        // 디스크 상 풀 경로
        Path dir = rootLocation.resolve(cleanedBasePath).normalize();
        Path target = dir.resolve(storedName);

        try {
            Files.createDirectories(dir); // 하위 디렉토리까지 생성
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패: " + e.getMessage(), e);
        }

        // DB에는 상대 경로만 저장 (예: posters/uuid.jpg, thumbnails/uuid.jpg, ppt/origin/uuid.pptx)
        return cleanedBasePath + "/" + storedName;
    }

    private String normalizeBasePath(String basePath) {
        if (basePath == null || basePath.isBlank()) return "";
        // 공백 제거
        String p = basePath.trim();

        // Windows-style \ → /
        p = p.replace('\\', '/');

        // 앞뒤 슬래시 제거
        while (p.startsWith("/")) p = p.substring(1);
        while (p.endsWith("/")) p = p.substring(0, p.length() - 1);

        return p;
    }

    private String getExtension(String name) {
        if (name == null) return null;
        int idx = name.lastIndexOf('.');
        return (idx == -1) ? null : name.substring(idx + 1);
    }
}