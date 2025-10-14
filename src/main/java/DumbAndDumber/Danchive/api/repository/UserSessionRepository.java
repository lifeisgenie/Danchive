package DumbAndDumber.Danchive.api.repository;

import DumbAndDumber.Danchive.api.entity.UserSession;
import DumbAndDumber.Danchive.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findByRefreshToken(String refreshToken);
    void deleteByUser(User user);
}
