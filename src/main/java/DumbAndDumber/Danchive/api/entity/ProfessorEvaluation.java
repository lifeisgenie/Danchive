package DumbAndDumber.Danchive.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "professor_evaluations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_prof_eval_exhibit_prof",
                        columnNames = {"exhibit_id", "professor_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ProfessorEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 작품 평가인지
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exhibit_id", nullable = false)
    @JsonIgnore
    private Exhibit exhibit;

    // 어떤 교수가 평가했는지
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professor_id", nullable = false)
    @JsonIgnore
    private User professor;

    // 1. 기술적 완성도 / 목표 구현 수준 (1~5)
    @Column(nullable = false)
    private int technicalScore;

    // 2. 문제 중요성 & 실용적 가치 / 파급력 (1~5)
    @Column(nullable = false)
    private int impactScore;

    // 3. 아이디어 창의성 / 혁신성 (1~5)
    @Column(nullable = false)
    private int creativityScore;

    @Column(length = 500)
    private String comment;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}