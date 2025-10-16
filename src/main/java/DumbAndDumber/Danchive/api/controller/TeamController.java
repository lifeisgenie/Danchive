package DumbAndDumber.Danchive.api.controller;

import DumbAndDumber.Danchive.api.dto.ApiResponse;
import DumbAndDumber.Danchive.api.dto.team.*;
import DumbAndDumber.Danchive.api.service.TeamService;
import DumbAndDumber.Danchive.api.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;
    public TeamController(TeamService teamService) { this.teamService = teamService; }

    @PostMapping
    public ResponseEntity<ApiResponse<TeamCreateResponse>> createTeam(
            @Valid @RequestBody TeamCreateRequest req) {
        Long userId = SecurityUtil.getCurrentUserId();
        var data = teamService.createTeam(userId, req.getTeamName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "팀이 생성되었습니다.", data));
    }

    @PostMapping("/{teamId}/invites")
    public ResponseEntity<ApiResponse<TeamInviteSendResponse>> sendInvite(
            @PathVariable Long teamId,
            @Valid @RequestBody TeamInviteRequest req) {
        Long userId = SecurityUtil.getCurrentUserId();
        var data = teamService.sendInvite(userId, teamId, req.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "초대가 전송되었습니다.", data));
    }

    @PostMapping("/invites/{inviteId}/accept")
    public ResponseEntity<ApiResponse<TeamAcceptResponse>> acceptInvite(
            @PathVariable Long inviteId) {
        Long userId = SecurityUtil.getCurrentUserId();
        var data = teamService.acceptInvite(userId, inviteId);
        return ResponseEntity.ok(new ApiResponse<>(true, "팀에 합류했습니다.", data));
    }

    @DeleteMapping("/{teamId}/members/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable Long teamId,
            @PathVariable("userId") Long targetUserId) {
        Long userId = SecurityUtil.getCurrentUserId();
        teamService.removeMember(userId, teamId, targetUserId);
        return ResponseEntity.ok(new ApiResponse<>(true, "팀원이 제거되었습니다.", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<TeamMeResponse>> getMyTeam() {
        Long userId = SecurityUtil.getCurrentUserId();
        var data = teamService.getMyTeam(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "내 팀 정보 조회 성공", data));
    }

    // 대기중 초대 목록
    @GetMapping("/invites/me")
    public ResponseEntity<ApiResponse<List<TeamInviteSummaryDto>>> getMyInvites(
            @RequestParam(name = "status", required = false) String status) {

        Long userId = SecurityUtil.getCurrentUserId();
        List<TeamInviteSummaryDto> data = teamService.getMyInvites(userId, status);
        return ResponseEntity.ok(new ApiResponse<>(true, "내 초대 목록 조회 성공", data));
    }
}