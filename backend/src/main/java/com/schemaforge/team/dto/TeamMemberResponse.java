package com.schemaforge.team.dto;

import com.schemaforge.team.entity.TeamRole;
import java.time.Instant;
import java.util.UUID;

public record TeamMemberResponse(
        UUID id,
        UUID teamId,
        UUID userId,
        String email,
        String fullName,
        TeamRole role,
        Instant joinedAt
) {}
