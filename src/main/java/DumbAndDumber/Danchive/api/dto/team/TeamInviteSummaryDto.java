package DumbAndDumber.Danchive.api.dto.team;

import java.time.Instant;

public record TeamInviteSummaryDto(Long inviteId, Long teamId, String teamName, String status, Instant invitedAt) {}