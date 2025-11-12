package DumbAndDumber.Danchive.api.dto.exhibit;

import DumbAndDumber.Danchive.api.entity.ExhibitCategory;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExhibitUpdateRequest {
    // 팀원/팀장은 텍스트 수정 가능
    private String title;
    private String intro;
    private String shortIntro;
    private Set<ExhibitCategory> categories;

    // 파일은 팀장만 교체 가능
    private MultipartFile poster;
    private MultipartFile ppt;
}
