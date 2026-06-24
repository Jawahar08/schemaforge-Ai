package com.schemaforge.notification.exception;

import com.schemaforge.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class NotificationNotFoundException extends ResourceNotFoundException {

    public NotificationNotFoundException(UUID id) {
        super("Notification not found with id: " + id);
    }
}
