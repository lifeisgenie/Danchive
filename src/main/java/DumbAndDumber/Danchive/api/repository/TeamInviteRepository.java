package DumbAndDumber.Danchive.api.repository;

import DumbAndDumber.Danchive.api.entity.TeamInvite;
import DumbAndDumber.Danchive.api.entity.InviteStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamInviteRepository extends JpaRepository<TeamInvite, Long> {
    Optional<TeamInvite> findByIdAndStatus(Long id, InviteStatus status);
}
