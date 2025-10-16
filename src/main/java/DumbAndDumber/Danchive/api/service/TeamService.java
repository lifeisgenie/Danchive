package DumbAndDumber.Danchive.api.service;

import DumbAndDumber.Danchive.api.dto.team.*;
import DumbAndDumber.Danchive.api.entity.*;
import DumbAndDumber.Danchive.api.store.TeamStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class TeamService {
    private final TeamStore store;

    public TeamService(TeamStore store) { this.store = store; }

    @Transactional
    public TeamCreatedResponse createTeam(Long userId, String teamName) {
        if (store.alreadyInAnyTeam(userId)) throw new IllegalStateException("이미 다른 팀에 소속되어 있습니다.");

        Team team = store.saveTeam(new Team(teamName));
        // 리더 단일성 서비스 레벨 체크
        if (store.hasLeader(team)) throw new IllegalStateException("이미 팀장이 존재합니다.");
        store.saveMember(new TeamMember(team, userId, TeamRole.LEADER));

        return new TeamCreatedResponse(team.getId(), team.getName(), "leader");
    }

    @Transactional
    public InviteCreatedResponse inviteMember(Long inviterId, Long teamId, String email) {
        Team team = store.findTeamOrThrow(teamId);
        TeamMember me = store.findMembershipOrThrow(team, inviterId);
        if (me.getRole() != TeamRole.LEADER) throw new SecurityException("팀장만 초대할 수 있습니다.");
        if (store.pendingInviteExistsForEmail(email)) throw new IllegalStateException("이미 보류 중인 초대가 있습니다.");

        TeamInvite invite = store.saveInvite(new TeamInvite(team, email, inviterId, store.defaultInviteExpiry()));
        return new InviteCreatedResponse(invite.getId());
    }

    @Transactional
    public AcceptInviteResponse acceptInvite(Long userId, String myEmail, Long inviteId) {
        if (store.alreadyInAnyTeam(userId)) throw new IllegalStateException("이미 다른 팀에 소속되어 있습니다.");
        TeamInvite invite = store.findPendingInviteOrThrow(inviteId);
        if (invite.isExpired()) throw new IllegalStateException("초대가 만료되었습니다.");
        if (!invite.getEmail().equalsIgnoreCase(myEmail)) throw new SecurityException("초대받은 이메일과 일치하지 않습니다.");

        invite.accept();
        Team team = invite.getTeam();
        store.saveMember(new TeamMember(team, userId, TeamRole.MEMBER));
        return new AcceptInviteResponse(team.getId(), "member");
    }

    @Transactional
    public void removeMember(Long leaderUserId, Long teamId, Long targetUserId) {
        Team team = store.findTeamOrThrow(teamId);
        TeamMember leader = store.findMembershipOrThrow(team, leaderUserId);
        if (leader.getRole() != TeamRole.LEADER) throw new SecurityException("팀장만 팀원을 제거할 수 있습니다.");

        TeamMember target = store.findMembershipOrThrow(team, targetUserId);
        if (target.getRole() == TeamRole.LEADER) throw new IllegalStateException("팀장은 제거할 수 없습니다.");
        store.removeMember(target);
    }

    @Transactional(readOnly = true)
    public TeamMeResponse getMyTeam(Long userId, java.util.function.LongFunction<String> userNameResolver) {
        TeamMember myMembership = store.findMembershipOrThrowForUser(userId);
        Team team = myMembership.getTeam();

        var members = store.findMembers(team).stream().map(m -> {
            String name = (userNameResolver != null) ? userNameResolver.apply(m.getUserId()) : ("사용자" + m.getUserId());
            String role = (m.getRole() == TeamRole.LEADER) ? "leader" : "member";
            return new TeamMeResponse.Member(m.getUserId(), name, role);
        }).collect(Collectors.toList());

        return new TeamMeResponse(team.getId(), team.getName(), members);
    }
}