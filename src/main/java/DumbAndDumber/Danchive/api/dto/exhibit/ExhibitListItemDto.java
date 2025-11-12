package DumbAndDumber.Danchive.api.dto.exhibit;

import DumbAndDumber.Danchive.api.entity.ExhibitAward;
import DumbAndDumber.Danchive.api.entity.ExhibitCategory;
import lombok.*;

import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExhibitListItemDto {
    private Long id;
    private String term;
    private String title;
    private String teamName;
    private String thumbnailUrl;
    private String shortIntro;
    private Set<ExhibitCategory> categories;
    private Set<ExhibitAward> awards;
}
