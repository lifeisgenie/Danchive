package DumbAndDumber.Danchive.api.repository;

import DumbAndDumber.Danchive.api.entity.Team;
import DumbAndDumber.Danchive.api.entity.TeamMembership;
import DumbAndDumber.Danchive.api.entity.TeamRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMembershipRepository extends JpaRepository<TeamMembership, Long> {
    Optional<TeamMembership> findByUser_Id(Long userId);
    Optional<TeamMembership> findByTeam_IdAndUser_Id(Long teamId, Long userId);
    List<TeamMembership> findAllByTeam_Id(Long teamId);
    boolean existsByUser_Id(Long userId);
    boolean existsByTeam_IdAndRole(Long teamId, TeamRole role);
    List<TeamMembership> findByTeam(Team team);
}
