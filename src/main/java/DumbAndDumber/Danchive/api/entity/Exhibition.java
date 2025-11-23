package DumbAndDumber.Danchive.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "exhibitions") @Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Exhibition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // "2025-2" 처럼, Exhibit.term과 1:1 매핑
    @Column(nullable = false, length = 10, unique = true)
    private String term;

    @Column(nullable = false, length = 150)
    private String title;       // 전시회 제목 (예: 2025-2 소프트웨어학과 졸업작품 전시회)

    @Column(nullable = false, length = 200)
    private String place;       // 장소 (예: 덕성여대 국제회의장 3층 로비)

    @Column(nullable = false)
    private LocalDate date;     // 전시 날짜 (하루)

    @Column(length = 255)
    private String imageUrl;    // 전시 대표 이미지 (포스터 등)
}