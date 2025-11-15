package DumbAndDumber.Danchive.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity @Table(name="exhibits")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EntityListeners(AuditingEntityListener.class)
public class Exhibit {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=10) // 예: "2025-2"
    private String term;

    @Column(nullable=false, length=150)
    private String title;

    @Column(nullable=false, columnDefinition="TEXT")
    private String intro;

    @Column(nullable=false, length=200)
    private String shortIntro;

    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name="exhibit_categories", joinColumns=@JoinColumn(name="exhibit_id"))
    @Enumerated(EnumType.STRING)
    @Column(name="category", length=20)
    private Set<ExhibitCategory> categories = new LinkedHashSet<>();

    @ManyToOne(optional=false) @JoinColumn(name="team_id")
    private Team team;

    private String thumbnailUrl;   // 생성 시 포스터로부터 썸네일
    private String posterUrl;

    private String pptName;
    private String pptPreviewUrl;
    private String pptDownloadUrl;

    private long views;

    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name="exhibit_awards", joinColumns=@JoinColumn(name="exhibit_id"))
    @Enumerated(EnumType.STRING)
    @Column(name="award", length=20)
    private Set<ExhibitAward> awards = new LinkedHashSet<>();

    @Column(nullable=false)
    private boolean published = true;

    @CreatedDate
    @Column(nullable=false, updatable=false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable=false)
    private LocalDateTime updatedAt;
}