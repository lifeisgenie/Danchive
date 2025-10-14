package DumbAndDumber.Danchive.api.repository;

import DumbAndDumber.Danchive.domain.LogoutToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LogoutTokenRepository extends CrudRepository<LogoutToken, Long> {
    Optional<LogoutToken> findByToken(String token);
}
