package com.schemaforge.team.controller;

import com.schemaforge.common.dto.ApiResponse;
import com.schemaforge.team.dto.CreateInvitationRequest;
import com.schemaforge.team.dto.InvitationResponse;
import com.schemaforge.team.service.InvitationService;
import com.schemaforge.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Invitations", description = "Endpoints for managing workspace invitations")
public class InvitationController {

    private final InvitationService invitationService;

    @PostMapping("/api/teams/{teamId}/invitations")
    @Operation(summary = "Invite member", description = "Sends a pending email invitation to join a team workspace. Only team owner can invite.")
    public ResponseEntity<ApiResponse<InvitationResponse>> inviteMember(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID teamId,
            @Valid @RequestBody CreateInvitationRequest request
    ) {
        InvitationResponse response = invitationService.createInvitation(currentUser, teamId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Invitation sent successfully", response));
    }

    @GetMapping("/api/teams/{teamId}/invitations")
    @Operation(summary = "Get invitations", description = "Retrieves all invitations sent for a team workspace")
    public ResponseEntity<ApiResponse<List<InvitationResponse>>> getInvitations(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID teamId
    ) {
        List<InvitationResponse> response = invitationService.getInvitations(currentUser, teamId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/api/invitations/{token}/accept")
    @Operation(summary = "Accept invitation", description = "Accepts a pending workspace invitation, adding the user to the team.")
    public ResponseEntity<ApiResponse<Void>> acceptInvitation(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String token
    ) {
        invitationService.acceptInvitation(currentUser, token);
        return ResponseEntity.ok(ApiResponse.message("Invitation accepted successfully"));
    }

    @PostMapping("/api/invitations/{token}/reject")
    @Operation(summary = "Reject invitation", description = "Rejects a pending workspace invitation.")
    public ResponseEntity<ApiResponse<Void>> rejectInvitation(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String token
    ) {
        invitationService.rejectInvitation(currentUser, token);
        return ResponseEntity.ok(ApiResponse.message("Invitation rejected successfully"));
    }
}
