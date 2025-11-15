package DumbAndDumber.Danchive.api.dto.exhibit;

import DumbAndDumber.Danchive.api.entity.ExhibitAward;
import DumbAndDumber.Danchive.api.entity.ExhibitCategory;
import DumbAndDumber.Danchive.api.entity.TeamRole;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExhibitDetailDto {
    private Long id;
    private String term;
    private String title;
    private String intro;
    private Set<ExhibitCategory> categories;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class MemberDto {
        private String name;
        private TeamRole role;
    }
    private List<MemberDto> members;

    private String thumbnailUrl;
    private String posterUrl;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PptDto {
        private String name;
        private String previewUrl;
        private String downloadUrl;
    }
    private PptDto ppt;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class StatsDto {
        private long views;
        private long likes;
    }
    private StatsDto stats;

    private Set<ExhibitAward> awards;
}