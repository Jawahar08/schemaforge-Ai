package com.schemaforge.team.exception;

import com.schemaforge.common.exception.ResourceNotFoundException;
import java.util.UUID;

public class TeamNotFoundException extends ResourceNotFoundException {

    public TeamNotFoundException(UUID id) {
        super("Team not found with id: " + id);
    }
}
