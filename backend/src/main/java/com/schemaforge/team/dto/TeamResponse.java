package com.schemaforge.team.dto;

import com.schemaforge.team.entity.TeamPlan;
import java.time.Instant;
import java.util.UUID;

public record TeamResponse(
        UUID id,
        String name,
        String slug,
        String description,
        UUID ownerId,
        TeamPlan plan,
        Instant createdAt,
        Instant updatedAt
) {}
