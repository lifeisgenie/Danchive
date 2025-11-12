package DumbAndDumber.Danchive.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "exhibits",
        indexes = {
                @Index(name="idx_exhibits_term", columnList = "term"),
                @Index(name="idx_exhibits_title", columnList = "title")
        })
public class Exhibit {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // "2025-2" 형식
    @Column(nullable = false, length = 7)
    private String term;

    @Column(nullable = false, length = 150)
    private String title;

    @Lob
    @Column(nullable = false)
    private String intro; // 상세 소개

    // 다중 카테고리
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "exhibit_categories",
            joinColumns = @JoinColumn(name = "exhibit_id"))
    @Column(name = "category", nullable = false, length = 20)
    private Set<ExhibitCategory> categories = new LinkedHashSet<>();

    // 팀 연결 (등록/수정 권한 판단용)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Team team;

    // 포스터 & 썸네일
    @Column(length = 500)
    private String posterUrl;
    @Column(length = 500)
    private String thumbnailUrl;

    // PPT
    @Column(length = 200)
    private String pptName;
    @Column(length = 500)
    private String pptPreviewUrl;
    @Column(length = 500)
    private String pptDownloadUrl;

    // 통계
    @Column(nullable = false)
    private long views = 0L;

    // 수상 결과 (여러 상 동시 표기가능)
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name="exhibit_awards", joinColumns=@JoinColumn(name="exhibit_id"))
    @Column(name="award", length=20)
    private Set<ExhibitAward> awards = new LinkedHashSet<>();

    // 썸네일용 짧은 소개 (리스트 요약)
    @Column(length = 200)
    private String shortIntro;

    // 생성/수정 메타
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }
    @PreUpdate void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
