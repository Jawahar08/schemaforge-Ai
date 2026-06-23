package com.schemaforge.team.exception;

import com.schemaforge.common.exception.BadRequestException;

public class InvitationExpiredException extends BadRequestException {

    public InvitationExpiredException(String message) {
        super(message);
    }
}
