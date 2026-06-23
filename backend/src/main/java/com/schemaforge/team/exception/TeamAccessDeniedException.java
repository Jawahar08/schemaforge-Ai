package com.schemaforge.team.exception;

import com.schemaforge.common.exception.ForbiddenException;

public class TeamAccessDeniedException extends ForbiddenException {

    public TeamAccessDeniedException() {
        super("You do not have access to this team workspace");
    }

    public TeamAccessDeniedException(String message) {
        super(message);
    }
}
