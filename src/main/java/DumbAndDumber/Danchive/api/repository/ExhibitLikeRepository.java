package DumbAndDumber.Danchive.api.repository;

import DumbAndDumber.Danchive.api.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExhibitLikeRepository extends JpaRepository<ExhibitLike, Long> {
    long countByExhibit(Exhibit exhibit);
    Optional<ExhibitLike> findByExhibitAndUser(Exhibit exhibit, User user);
}