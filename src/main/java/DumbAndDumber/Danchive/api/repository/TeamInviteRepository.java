package DumbAndDumber.Danchive.api.repository;

import DumbAndDumber.Danchive.api.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface TeamInviteRepository extends JpaRepository<TeamInvite, Long> {
    Optional<TeamInvite> findByIdAndStatus(Long id, InviteStatus status);
    List<TeamInvite> findAllByEmailIgnoreCaseAndStatus(String email, InviteStatus status);
}