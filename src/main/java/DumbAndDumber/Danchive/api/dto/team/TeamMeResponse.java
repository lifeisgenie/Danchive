package DumbAndDumber.Danchive.api.dto.team;

import java.util.List;

public record TeamMeResponse(Long teamId, String teamName, List<TeamMemberDto> members) {}