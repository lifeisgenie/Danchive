package DumbAndDumber.Danchive.api.dto.team;

public record AcceptInviteResponse(
        Long teamId,
        String roleInTeam // "member"
) {}
