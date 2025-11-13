package DumbAndDumber.Danchive.api.repository;

import DumbAndDumber.Danchive.api.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface TeamMembershipRepository extends JpaRepository<TeamMembership, Long> {
    boolean existsByUser_Id(Long userId);
    Optional<TeamMembership> findByTeam_IdAndUser_Id(Long teamId, Long userId);
    Optional<TeamMembership> findByUser_Id(Long userId);
    List<TeamMembership> findAllByTeam_Id(Long teamId);
    List<TeamMembership> findByTeam(Team team);
}