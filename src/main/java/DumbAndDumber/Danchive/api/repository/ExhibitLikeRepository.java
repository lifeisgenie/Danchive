package DumbAndDumber.Danchive.api.repository;

import DumbAndDumber.Danchive.api.entity.ExhibitLike;
import DumbAndDumber.Danchive.api.entity.Exhibit;
import DumbAndDumber.Danchive.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExhibitLikeRepository extends JpaRepository<ExhibitLike, Long> {
    long countByExhibit(Exhibit exhibit);
    Optional<ExhibitLike> findByExhibitAndUser(Exhibit exhibit, User user);
}
