package DumbAndDumber.Danchive.api.repository;

import DumbAndDumber.Danchive.api.entity.Exhibition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {

    // 최신 전시회가 위로 오도록
    List<Exhibition> findAllByOrderByDateDesc();

    // term으로 전시회 조회 (필요하면)
    Optional<Exhibition> findByTerm(String term);
}