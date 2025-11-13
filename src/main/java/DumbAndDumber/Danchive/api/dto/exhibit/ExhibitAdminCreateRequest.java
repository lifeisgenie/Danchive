package DumbAndDumber.Danchive.api.dto.exhibit;

import DumbAndDumber.Danchive.api.entity.ExhibitCategory;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Getter @Setter
public class ExhibitAdminCreateRequest {
    @NotBlank private String term;          // "2024-2" 처럼 과거 학기도 허용
    @NotBlank private String title;
    private String shortIntro;
    private Set<ExhibitCategory> categories;
    private MultipartFile thumbnail;
    private List<MultipartFile> images;
    private MultipartFile artifact;
    private Boolean awardGold;              // 수상 플래그 등
}