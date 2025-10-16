package DumbAndDumber.Danchive.api.dto.team;

public record TeamCreatedResponse(
        Long teamId,
        String teamName,
        String roleInTeam // "leader"
) {}
