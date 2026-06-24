package com.schemaforge.notification.service;

import com.schemaforge.notification.dto.NotificationResponse;
import com.schemaforge.notification.entity.NotificationType;
import com.schemaforge.user.entity.User;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface NotificationService {

    /**
     * Returns all notifications for the authenticated user, newest first.
     */
    List<NotificationResponse> getMyNotifications(User currentUser);

    /**
     * Returns only unread notifications for the authenticated user, newest first.
     */
    List<NotificationResponse> getUnreadNotifications(User currentUser);

    /**
     * Marks a single notification as read. User must own the notification.
     */
    NotificationResponse markAsRead(User currentUser, UUID notificationId);

    /**
     * Marks ALL of the user's notifications as read. Returns count of updated rows.
     */
    int markAllAsRead(User currentUser);

    /**
     * Deletes a single notification. User must own the notification.
     */
    void deleteNotification(User currentUser, UUID notificationId);

    /**
     * Internal method: creates and persists a new notification.
     * Called by other service modules (Comments, Teams, AI, Exports).
     *
     * @param userId   recipient user id
     * @param type     notification type
     * @param title    short display title
     * @param message  longer description
     * @param metadata arbitrary key-value payload (e.g. projectId, schemaId)
     */
    void createNotification(UUID userId, NotificationType type, String title, String message, Map<String, String> metadata);
}
