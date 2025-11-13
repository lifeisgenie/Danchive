package DumbAndDumber.Danchive.api.dto.exhibit;

import DumbAndDumber.Danchive.api.entity.ExhibitCategory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Getter @Setter
public class ExhibitAdminUpdateRequest {
    private String term;                    // 변경 시에만 값 전달
    private String title;
    private String shortIntro;
    private Set<ExhibitCategory> categories;
    private Boolean awardGold;
    private MultipartFile thumbnail;
    private List<MultipartFile> images;
    private MultipartFile artifact;
}