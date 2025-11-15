package DumbAndDumber.Danchive.api.dto.exhibit;

import DumbAndDumber.Danchive.api.entity.ExhibitCategory;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExhibitUpdateRequest {
    private String title;
    private String intro;
    private String shortIntro;
    private Set<ExhibitCategory> categories;

    private MultipartFile poster; // 리더만 반영
    private MultipartFile ppt;    // 리더만 반영
}