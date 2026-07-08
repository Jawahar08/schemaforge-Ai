package com.schemaforge.dashboard.controller;

import com.schemaforge.common.dto.ApiResponse;
import com.schemaforge.dashboard.dto.DashboardSummaryResponse;
import com.schemaforge.dashboard.service.DashboardService;
import com.schemaforge.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Authenticated user dashboard analytics and summary")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @Operation(
            summary = "Get dashboard summary",
            description = "Returns project counts, schema counts, team counts, unread notifications, "
                    + "and 5 most recent projects and schemas — all scoped to the authenticated user."
    )
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getSummary(
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getSummary(currentUser)));
    }
}