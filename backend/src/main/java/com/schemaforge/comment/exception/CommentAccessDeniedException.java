package com.schemaforge.comment.exception;

import com.schemaforge.common.exception.ForbiddenException;

public class CommentAccessDeniedException extends ForbiddenException {

    public CommentAccessDeniedException() {
        super("You do not have permission to access or modify this comment");
    }
}
