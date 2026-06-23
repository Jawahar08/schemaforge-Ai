package com.schemaforge.team.exception;

import com.schemaforge.common.exception.ResourceNotFoundException;
import java.util.UUID;

public class InvitationNotFoundException extends ResourceNotFoundException {

    public InvitationNotFoundException(UUID id) {
        super("Invitation not found with id: " + id);
    }

    public InvitationNotFoundException(String token) {
        super("Invitation not found with token: " + token);
    }
}
