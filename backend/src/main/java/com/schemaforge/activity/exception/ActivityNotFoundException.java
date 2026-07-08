package com.schemaforge.activity.exception;

import com.schemaforge.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class ActivityNotFoundException extends ResourceNotFoundException {

    public ActivityNotFoundException(UUID id) {
        super("Activity not found with id: " + id);
    }
}