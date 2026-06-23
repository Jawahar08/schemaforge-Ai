package com.schemaforge.team.controller;

import com.schemaforge.common.dto.ApiResponse;
import com.schemaforge.team.dto.TeamMemberResponse;
import com.schemaforge.team.service.TeamMemberService;
import com.schemaforge.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/teams/{teamId}/members")
@RequiredArgsConstructor
@Tag(name = "Team Members", description = "Endpoints for managing workspace members")
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    @GetMapping
    @Operation(summary = "Get team members", description = "Retrieves all members of a team workspace")
    public ResponseEntity<ApiResponse<List<TeamMemberResponse>>> getTeamMembers(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID teamId
    ) {
        List<TeamMemberResponse> response = teamMemberService.getTeamMembers(currentUser, teamId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{memberId}")
    @Operation(summary = "Remove team member", description = "Removes a member from the team. Only the team owner can perform this action.")
    public ResponseEntity<ApiResponse<Void>> removeTeamMember(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID teamId,
            @PathVariable UUID memberId
    ) {
        teamMemberService.removeTeamMember(currentUser, teamId, memberId);
        return ResponseEntity.ok(ApiResponse.message("Team member removed successfully"));
    }
}
