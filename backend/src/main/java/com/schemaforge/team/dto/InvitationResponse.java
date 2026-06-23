package com.schemaforge.team.dto;

import com.schemaforge.team.entity.InvitationStatus;
import com.schemaforge.team.entity.TeamRole;
import java.time.Instant;
import java.util.UUID;

public record InvitationResponse(
        UUID id,
        UUID teamId,
        UUID invitedById,
        String email,
        TeamRole role,
        String token,
        InvitationStatus status,
        Instant expiresAt,
        Instant createdAt
) {}
