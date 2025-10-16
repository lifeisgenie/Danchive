package DumbAndDumber.Danchive.api.controller;

import DumbAndDumber.Danchive.api.dto.team.*;
import DumbAndDumber.Danchive.api.service.TeamService;
import DumbAndDumber.Danchive.api.util.SecurityUtil;
import DumbAndDumber.Danchive.api.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teams")
public class TeamController {
    private final TeamService teamService;
    public TeamController(TeamService teamService) { this.teamService = teamService; }

    @PostMapping
    public ResponseEntity<ApiResponse<TeamCreatedResponse>> createTeam(@RequestBody @Valid CreateTeamRequest req) {
        Long me = SecurityUtil.currentUserId();
        var data = teamService.createTeam(me, req.teamName());
        return ResponseEntity.status(201).body(ApiResponse.success("팀이 생성되었습니다.", data));
    }

    @PostMapping("/{teamId}/invites")
    public ResponseEntity<ApiResponse<InviteCreatedResponse>> invite(@PathVariable Long teamId,
                                                                     @RequestBody @Valid InviteRequest req) {
        Long me = SecurityUtil.currentUserId();
        var data = teamService.inviteMember(me, teamId, req.email());
        return ResponseEntity.status(201).body(ApiResponse.success("초대가 전송되었습니다.", data));
    }

    @PostMapping("/invites/{inviteId}/accept")
    public ResponseEntity<ApiResponse<AcceptInviteResponse>> accept(@PathVariable Long inviteId) {
        Long me = SecurityUtil.currentUserId();
        String myEmail = getCurrentUserEmail(); // 실제 프로젝트에서 Principal에서 획득
        var data = teamService.acceptInvite(me, myEmail, inviteId);
        return ResponseEntity.ok(ApiResponse.success("팀에 합류했습니다.", data));
    }

    @DeleteMapping("/{teamId}/members/{userId}")
    public ResponseEntity<ApiResponse<Void>> kick(@PathVariable Long teamId, @PathVariable Long userId) {
        Long me = SecurityUtil.currentUserId();
        teamService.removeMember(me, teamId, userId);
        return ResponseEntity.ok(ApiResponse.success("팀원이 제거되었습니다.", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<TeamMeResponse>> myTeam() {
        Long me = SecurityUtil.currentUserId();
        var data = teamService.getMyTeam(me, this::resolveUserName);
        return ResponseEntity.ok(ApiResponse.success("내 팀 정보 조회 성공", data));
    }

    private String resolveUserName(long userId) {
        // TODO: UserRepository 통해 조회 (예: userRepo.findById(userId).map(User::getName).orElse("알수없음"))
        return "사용자" + userId;
    }
    private String getCurrentUserEmail() {
        // TODO: ((CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail()
        return "member@dku.ac.kr";
    }
}