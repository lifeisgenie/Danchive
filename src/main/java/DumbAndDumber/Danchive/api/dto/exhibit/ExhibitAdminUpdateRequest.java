package DumbAndDumber.Danchive.api.dto.exhibit;

import DumbAndDumber.Danchive.api.entity.ExhibitCategory;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Getter @Setter @NoArgsConstructor
@AllArgsConstructor @Builder
public class ExhibitAdminUpdateRequest {
    private String term;                         // 예: "2024-2"
    private String title;
    private String intro;
    private String shortIntro;
    private Set<ExhibitCategory> categories;
    private MultipartFile poster;
    private MultipartFile ppt;
    private Boolean published;                   // 공개/비공개
}