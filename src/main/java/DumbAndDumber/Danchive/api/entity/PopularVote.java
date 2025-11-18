package DumbAndDumber.Danchive.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "popular_votes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_popular_vote_term_user",
                        columnNames = {"term", "voter_user_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class PopularVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어느 학기의 투표인지 (Exhibit.term 과 동일 포맷, 예: "2025-2")
    @Column(nullable = false, length = 10)
    private String term;

    // 어떤 작품에 대한 투표인지
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exhibit_id", nullable = false)
    @JsonIgnore
    private Exhibit exhibit;

    // 로그인 사용자면 user 저장, 방문객이면 null
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voter_user_id")
    @JsonIgnore
    private User voter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PopularVoterType voterType;

    // 1. 이번 전시회에서 가장 마음에 들었던 팀의 이름 (주관식)
    @Column(nullable = false, length = 150)
    private String favoriteTeamName;

    // 2. 아이디어 / 창의성 (1~5)
    @Column(nullable = false)
    private int creativityScore;

    // 3. 완성도 / 주제 전달력 (1~5)
    @Column(nullable = false)
    private int completionScore;

    // 방문객용 (선택값)
    @Column(length = 80)
    private String voterName;

    @Column(length = 100)
    private String voterContact;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}