package com.schemaforge.team.dto;

import com.schemaforge.team.entity.TeamPlan;
import java.util.UUID;

public record TeamSummaryResponse(
        UUID id,
        String name,
        String slug,
        String description,
        TeamPlan plan
) {}
