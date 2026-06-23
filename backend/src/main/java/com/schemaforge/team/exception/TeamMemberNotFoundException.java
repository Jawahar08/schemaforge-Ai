package com.schemaforge.team.exception;

import com.schemaforge.common.exception.ResourceNotFoundException;
import java.util.UUID;

public class TeamMemberNotFoundException extends ResourceNotFoundException {

    public TeamMemberNotFoundException(UUID id) {
        super("Team member not found with id: " + id);
    }

    public TeamMemberNotFoundException(UUID teamId, UUID userId) {
        super(String.format("User %s is not a member of team %s", userId, teamId));
    }
}
