package com.schemaforge.notification.entity;

/**
 * Notification type enum — values must match the CHECK constraint in V9 migration.
 */
public enum NotificationType {
    SCHEMA_GENERATED,
    COMMENT_ADDED,
    TEAM_INVITATION,
    EXPORT_READY,
    MENTION
}
