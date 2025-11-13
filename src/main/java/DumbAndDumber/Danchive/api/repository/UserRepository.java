package DumbAndDumber.Danchive.api.repository;

import DumbAndDumber.Danchive.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByAccessToken(String token);
}