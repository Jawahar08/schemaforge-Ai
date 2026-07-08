package com.schemaforge.dashboard.dto;

import java.util.List;

public record DashboardSummaryResponse(
        long totalProjects,
        long activeProjects,
        long archivedProjects,
        long totalSchemas,
        long totalTeams,
        long unreadNotifications,
        List<RecentProjectResponse> recentProjects,
        List<RecentSchemaResponse> recentSchemas
) {
}