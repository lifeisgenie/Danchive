package DumbAndDumber.Danchive.api.store;

import DumbAndDumber.Danchive.api.entity.*;
import DumbAndDumber.Danchive.api.repository.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TeamStore {
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamInviteRepository teamInviteRepository;

    public TeamStore(TeamRepository teamRepository,
                     TeamMemberRepository teamMemberRepository,
                     TeamInviteRepository teamInviteRepository) {
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.teamInviteRepository = teamInviteRepository;
    }

    @Transactional public Team saveTeam(Team team) { return teamRepository.save(team); }
    @Transactional public TeamMember saveMember(TeamMember m) { return teamMemberRepository.save(m); }
    @Transactional public TeamInvite saveInvite(TeamInvite i) { return teamInviteRepository.save(i); }
    @Transactional public void removeMember(TeamMember m) { teamMemberRepository.delete(m); }

    @Transactional(readOnly = true)
    public Team findTeamOrThrow(Long id) {
        return teamRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("팀이 존재하지 않습니다."));
    }

    @Transactional(readOnly = true)
    public TeamInvite findPendingInviteOrThrow(Long inviteId) {
        return teamInviteRepository.findByIdAndStatus(inviteId, InviteStatus.PENDING)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 초대입니다."));
    }

    @Transactional(readOnly = true)
    public List<TeamMember> findMembers(Team team) { return teamMemberRepository.findByTeam(team); }

    @Transactional(readOnly = true)
    public TeamMember findMembershipOrThrow(Team team, Long userId) {
        return teamMemberRepository.findByTeamAndUserId(team, userId)
                .orElseThrow(() -> new IllegalStateException("팀 소속이 아닙니다."));
    }

    @Transactional(readOnly = true)
    public TeamMember findMembershipOrThrowForUser(Long userId) {
        return teamMemberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("아직 어떤 팀에도 소속되지 않았습니다."));
    }

    // 헬퍼
    public boolean alreadyInAnyTeam(Long userId) { return teamMemberRepository.existsByUserId(userId); }
    public boolean pendingInviteExistsForEmail(String email) { return teamInviteRepository.existsByEmailAndStatus(email, InviteStatus.PENDING); }
    public boolean hasLeader(Team team) { return teamMemberRepository.existsByTeamAndRole(team, TeamRole.LEADER); }
    public LocalDateTime defaultInviteExpiry() { return LocalDateTime.now().plusDays(7); }
}
