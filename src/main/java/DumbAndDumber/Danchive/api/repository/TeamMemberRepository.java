package DumbAndDumber.Danchive.api.repository;

import DumbAndDumber.Danchive.api.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    Optional<TeamMember> findByTeamAndUserId(Team team, Long userId);
    Optional<TeamMember> findByUserId(Long userId);
    List<TeamMember> findByTeam(Team team);

    boolean existsByUserId(Long userId);                // 한 유저=한 팀 제약
    boolean existsByTeamAndRole(Team team, TeamRole role); // 리더 1명 보장(서비스 레벨 확인)
}
