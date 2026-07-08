package com.schemaforge.activity.dto;

import com.schemaforge.activity.entity.ActivityEntityType;
import com.schemaforge.activity.entity.ActivityType;
import com.schemaforge.user.entity.User;

import java.util.Map;
import java.util.UUID;

/**
 * Internal application API for recording activity events.
 * Not exposed as a REST endpoint.
 */
public record RecordActivityRequest(
        User actor,
        UUID projectId,
        UUID teamId,
        UUID schemaId,
        ActivityType activityType,
        ActivityEntityType entityType,
        UUID entityId,
        String title,
        String description,
        Map<String, Object> metadata
) {

    public static RecordActivityRequest forProject(
            User actor,
            UUID projectId,
            ActivityType activityType,
            UUID entityId,
            String title,
            Map<String, Object> metadata
    ) {
        return new RecordActivityRequest(
                actor, projectId, null, null,
                activityType, ActivityEntityType.PROJECT,
                entityId, title, null, metadata
        );
    }

    public static RecordActivityRequest forSchema(
            User actor,
            UUID projectId,
            UUID schemaId,
            ActivityType activityType,
            UUID entityId,
            String title,
            Map<String, Object> metadata
    ) {
        return new RecordActivityRequest(
                actor, projectId, null, schemaId,
                activityType, ActivityEntityType.SCHEMA,
                entityId, title, null, metadata
        );
    }

    public static RecordActivityRequest forTeam(
            User actor,
            UUID teamId,
            ActivityType activityType,
            ActivityEntityType entityType,
            UUID entityId,
            String title,
            Map<String, Object> metadata
    ) {
        return new RecordActivityRequest(
                actor, null, teamId, null,
                activityType, entityType,
                entityId, title, null, metadata
        );
    }

    public static RecordActivityRequest forExport(
            User actor,
            UUID projectId,
            ActivityType activityType,
            UUID entityId,
            String title,
            Map<String, Object> metadata
    ) {
        return new RecordActivityRequest(
                actor, projectId, null, null,
                activityType, ActivityEntityType.EXPORT,
                entityId, title, null, metadata
        );
    }

    public static RecordActivityRequest forComment(
            User actor,
            UUID projectId,
            ActivityType activityType,
            UUID entityId,
            String title,
            Map<String, Object> metadata
    ) {
        return new RecordActivityRequest(
                actor, projectId, null, null,
                activityType, ActivityEntityType.COMMENT,
                entityId, title, null, metadata
        );
    }
}