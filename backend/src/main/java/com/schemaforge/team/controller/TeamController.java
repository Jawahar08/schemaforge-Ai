package com.schemaforge.team.controller;

import com.schemaforge.common.dto.ApiResponse;
import com.schemaforge.team.dto.CreateTeamRequest;
import com.schemaforge.team.dto.TeamResponse;
import com.schemaforge.team.dto.TeamSummaryResponse;
import com.schemaforge.team.dto.UpdateTeamRequest;
import com.schemaforge.team.service.TeamService;
import com.schemaforge.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
@Tag(name = "Teams", description = "Endpoints for managing collaborative team workspaces")
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    @Operation(summary = "Create a team", description = "Creates a new team workspace owned by the authenticated user")
    public ResponseEntity<ApiResponse<TeamResponse>> createTeam(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CreateTeamRequest request
    ) {
        TeamResponse response = teamService.createTeam(currentUser, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Team created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get teams", description = "Retrieves all teams the authenticated user belongs to")
    public ResponseEntity<ApiResponse<List<TeamSummaryResponse>>> getTeams(@AuthenticationPrincipal User currentUser) {
        List<TeamSummaryResponse> response = teamService.getTeams(currentUser);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{teamId}")
    @Operation(summary = "Get team by id", description = "Retrieves details of a single team workspace")
    public ResponseEntity<ApiResponse<TeamResponse>> getTeam(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID teamId
    ) {
        TeamResponse response = teamService.getTeamById(currentUser, teamId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{teamId}")
    @Operation(summary = "Update a team", description = "Updates details of a team workspace. Only team owners can edit.")
    public ResponseEntity<ApiResponse<TeamResponse>> updateTeam(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID teamId,
            @Valid @RequestBody UpdateTeamRequest request
    ) {
        TeamResponse response = teamService.updateTeam(currentUser, teamId, request);
        return ResponseEntity.ok(ApiResponse.success("Team updated successfully", response));
    }

    @DeleteMapping("/{teamId}")
    @Operation(summary = "Delete a team", description = "Deletes a team workspace. Only team owners can delete.")
    public ResponseEntity<ApiResponse<Void>> deleteTeam(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID teamId
    ) {
        teamService.deleteTeam(currentUser, teamId);
        return ResponseEntity.ok(ApiResponse.message("Team deleted successfully"));
    }
}
