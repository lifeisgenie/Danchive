package DumbAndDumber.Danchive.api.repository;

import DumbAndDumber.Danchive.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByAccessToken(String accessToken);
}
