package com.schemaforge.team.dto;

import jakarta.validation.constraints.Size;

public record UpdateTeamRequest(
        @Size(min = 2, max = 100, message = "Team name must be between 2 and 100 characters")
        String name,

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description
) {}
