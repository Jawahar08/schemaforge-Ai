package com.schemaforge.comment.exception;

import com.schemaforge.common.exception.ResourceNotFoundException;
import java.util.UUID;

public class CommentNotFoundException extends ResourceNotFoundException {

    public CommentNotFoundException(UUID id) {
        super("Comment not found with id: " + id);
    }
}
