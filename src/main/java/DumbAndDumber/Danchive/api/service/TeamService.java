package DumbAndDumber.Danchive.api.service;

import DumbAndDumber.Danchive.api.dto.team.*;
import DumbAndDumber.Danchive.api.entity.*;
import DumbAndDumber.Danchive.api.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMembershipRepository membershipRepository;
    private final TeamInviteRepository inviteRepository;
    private final UserRepository userRepository;
    private final ExhibitRepository exhibitRepository;

    public TeamService(TeamRepository teamRepository,
                       TeamMembershipRepository membershipRepository,
                       TeamInviteRepository inviteRepository,
                       UserRepository userRepository,
                       ExhibitRepository exhibitRepository) {
        this.teamRepository = teamRepository;
        this.membershipRepository = membershipRepository;
        this.inviteRepository = inviteRepository;
        this.userRepository = userRepository;
        this.exhibitRepository = exhibitRepository;
    }

    public TeamCreateResponse createTeam(Long currentUserId, String teamName) {
        if (teamRepository.existsByName(teamName)) {
            throw new IllegalArgumentException("이미 존재하는 팀 이름입니다.");
        }
        if (membershipRepository.existsByUser_Id(currentUserId)) {
            throw new IllegalStateException("이미 소속된 팀이 있습니다.");
        }
        Team team = teamRepository.save(new Team(teamName));
        User me = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
        membershipRepository.save(new TeamMembership(team, me, TeamRole.LEADER));
        return new TeamCreateResponse(team.getId(), teamName, TeamRole.LEADER.toWire());
    }

    public void deleteTeam(Long currentUserId, Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NoSuchElementException("team not found"));
        TeamMembership myMembership = membershipRepository
                .findByTeam_IdAndUser_Id(teamId, currentUserId)
                .orElseThrow(() -> new SecurityException("not a team member"));
        if (myMembership.getRole() != TeamRole.LEADER) {
            throw new SecurityException("only team leader can delete team");
        }
        boolean hasExhibits = exhibitRepository.existsByTeam(team);
        if (hasExhibits) {
            throw new IllegalStateException("작품이 등록된 팀은 삭제할 수 없습니다.");
        }
        inviteRepository.deleteByTeam(team);
        membershipRepository.deleteByTeam(team);
        teamRepository.delete(team);
    }


    public TeamInviteSendResponse sendInvite(Long currentUserId, Long teamId, String email) {
        ensureLeader(teamId, currentUserId);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NoSuchElementException("팀을 찾을 수 없습니다."));
        TeamInvite invite = inviteRepository.save(new TeamInvite(team, email, currentUserId));
        return new TeamInviteSendResponse(invite.getId());
    }

    public TeamAcceptResponse acceptInvite(Long currentUserId, Long inviteId) {
        TeamInvite invite = inviteRepository.findByIdAndStatus(inviteId, InviteStatus.PENDING)
                .orElseThrow(() -> new NoSuchElementException("유효하지 않은 초대입니다."));

        User me = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
        if (me.getEmail() == null || !me.getEmail().equalsIgnoreCase(invite.getEmail())) {
            throw new IllegalStateException("초대받은 이메일과 현재 사용자 이메일이 일치하지 않습니다.");
        }
        if (membershipRepository.existsByUser_Id(currentUserId)) {
            throw new IllegalStateException("이미 소속된 팀이 있습니다.");
        }

        Team team = invite.getTeam();
        membershipRepository.save(new TeamMembership(team, me, TeamRole.MEMBER));
        invite.accept();
        return new TeamAcceptResponse(team.getId(), TeamRole.MEMBER.toWire());
    }

    public void removeMember(Long currentUserId, Long teamId, Long targetUserId) {
        ensureLeader(teamId, currentUserId);
        TeamMembership target = membershipRepository.findByTeam_IdAndUser_Id(teamId, targetUserId)
                .orElseThrow(() -> new NoSuchElementException("해당 팀원을 찾을 수 없습니다."));
        if (target.getRole() == TeamRole.LEADER) {
            throw new IllegalStateException("팀장은 제거할 수 없습니다.");
        }
        membershipRepository.delete(target);
    }

    public TeamMeResponse getMyTeam(Long currentUserId) {
        TeamMembership my = membershipRepository.findByUser_Id(currentUserId)
                .orElseThrow(() -> new NoSuchElementException("소속된 팀이 없습니다."));
        Long teamId = my.getTeam().getId();
        Team team = my.getTeam();
        List<TeamMembership> members = membershipRepository.findAllByTeam_Id(teamId);

        List<TeamMemberDto> memberDtos = members.stream().map(m ->
                new TeamMemberDto(
                        m.getUser().getId(),
                        m.getUser().getName(),
                        m.getRole().toWire() // "leader" / "member"
                )
        ).toList();

        return new TeamMeResponse(team.getId(), team.getName(), memberDtos);
    }

    private void ensureLeader(Long teamId, Long userId) {
        TeamMembership membership = membershipRepository.findByTeam_IdAndUser_Id(teamId, userId)
                .orElseThrow(() -> new IllegalStateException("팀에 소속되지 않았습니다."));
        if (membership.getRole() != TeamRole.LEADER) {
            throw new SecurityException("팀 리더만 수행할 수 있습니다.");
        }
    }

    public List<TeamInviteSummaryDto> getMyInvites(Long currentUserId, String statusOpt) {
        User me = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        InviteStatus status;
        if (statusOpt == null || statusOpt.isBlank()) {
            status = InviteStatus.PENDING;
        } else {
            // "pending"/"accepted"... (소문자 허용)
            status = InviteStatus.fromWire(statusOpt);
        }

        List<TeamInvite> invites = inviteRepository.findAllByEmailIgnoreCaseAndStatus(me.getEmail(), status);
        return invites.stream().map(inv ->
                new TeamInviteSummaryDto(
                        inv.getId(),
                        inv.getTeam().getId(),
                        inv.getTeam().getName(),
                        inv.getStatus().toWire(),
                        inv.getCreatedAt()
                )
        ).toList();
    }
}