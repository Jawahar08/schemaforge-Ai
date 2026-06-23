package com.schemaforge.team.service;

import com.schemaforge.team.dto.CreateInvitationRequest;
import com.schemaforge.team.dto.InvitationResponse;
import com.schemaforge.user.entity.User;

import java.util.List;
import java.util.UUID;

public interface InvitationService {

    InvitationResponse createInvitation(User currentUser, UUID teamId, CreateInvitationRequest request);

    List<InvitationResponse> getInvitations(User currentUser, UUID teamId);

    void acceptInvitation(User currentUser, String token);

    void rejectInvitation(User currentUser, String token);
}
