package DumbAndDumber.Danchive.api.repository;

import DumbAndDumber.Danchive.api.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    boolean existsByName(String name);
    Optional<Team> findById(Long id);
}
