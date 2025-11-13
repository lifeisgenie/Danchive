package DumbAndDumber.Danchive.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity @Table(name="exhibits")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Exhibit {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=10) // "2025-2"
    private String term;

    @Column(nullable=false, length=150)
    private String title;

    @Column(nullable=false, columnDefinition="TEXT")
    private String intro;

    @Column(nullable=false, length=200)
    private String shortIntro;

    @Builder.Default
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name="exhibit_categories", joinColumns=@JoinColumn(name="exhibit_id"))
    @Enumerated(EnumType.STRING)
    @Column(name="category", length=20)
    private Set<ExhibitCategory> categories = new LinkedHashSet<>();

    @ManyToOne(optional=false) @JoinColumn(name="team_id")
    private Team team;

    private String thumbnailUrl;
    private String posterUrl;

    private String pptName;
    private String pptPreviewUrl;
    private String pptDownloadUrl;

    private long views;

    @Builder.Default
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name="exhibit_awards", joinColumns=@JoinColumn(name="exhibit_id"))
    @Enumerated(EnumType.STRING)
    @Column(name="award", length=10)
    private Set<ExhibitAward> awards = new LinkedHashSet<>();
}