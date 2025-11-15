package DumbAndDumber.Danchive.api.dto.exhibit;

import DumbAndDumber.Danchive.api.entity.ExhibitCategory;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExhibitCreateRequest {
    @NotBlank private String term;                 // 팀장은 현재 학기만, 관리자는 임의 학기
    @NotBlank @Size(max=150) private String title;
    @NotBlank private String intro;
    @NotBlank @Size(max=200) private String shortIntro;
    @NotEmpty private Set<ExhibitCategory> categories;

    private MultipartFile poster; // required
    private MultipartFile ppt;    // optional
}
