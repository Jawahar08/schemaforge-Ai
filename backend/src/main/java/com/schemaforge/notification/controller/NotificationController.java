package com.schemaforge.notification.controller;

import com.schemaforge.common.dto.ApiResponse;
import com.schemaforge.notification.dto.NotificationResponse;
import com.schemaforge.notification.service.NotificationService;
import com.schemaforge.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Endpoints for managing in-app user notifications")
public class NotificationController {

    private final NotificationService notificationService;

    // -------------------------------------------------------------------------
    // GET /api/notifications
    // -------------------------------------------------------------------------

    @GetMapping
    @Operation(
            summary = "Get all notifications",
            description = "Returns all notifications for the authenticated user, ordered newest first."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notifications retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications(
            @AuthenticationPrincipal User currentUser
    ) {
        List<NotificationResponse> notifications = notificationService.getMyNotifications(currentUser);
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    // -------------------------------------------------------------------------
    // GET /api/notifications/unread
    // -------------------------------------------------------------------------

    @GetMapping("/unread")
    @Operation(
            summary = "Get unread notifications",
            description = "Returns only unread notifications for the authenticated user, ordered newest first."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Unread notifications retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUnreadNotifications(
            @AuthenticationPrincipal User currentUser
    ) {
        List<NotificationResponse> notifications = notificationService.getUnreadNotifications(currentUser);
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    // -------------------------------------------------------------------------
    // PATCH /api/notifications/{notificationId}/read
    // -------------------------------------------------------------------------

    @PatchMapping("/{notificationId}/read")
    @Operation(
            summary = "Mark notification as read",
            description = "Marks a single notification as read. The authenticated user must own the notification."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notification marked as read"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(
            @AuthenticationPrincipal User currentUser,
            @Parameter(description = "UUID of the notification to mark as read")
            @PathVariable UUID notificationId
    ) {
        NotificationResponse response = notificationService.markAsRead(currentUser, notificationId);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", response));
    }

    // -------------------------------------------------------------------------
    // PATCH /api/notifications/read-all
    // -------------------------------------------------------------------------

    @PatchMapping("/read-all")
    @Operation(
            summary = "Mark all notifications as read",
            description = "Marks all unread notifications for the authenticated user as read."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "All notifications marked as read")
    })
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal User currentUser
    ) {
        int updated = notificationService.markAllAsRead(currentUser);
        return ResponseEntity.ok(ApiResponse.message("Marked " + updated + " notification(s) as read"));
    }

    // -------------------------------------------------------------------------
    // DELETE /api/notifications/{notificationId}
    // -------------------------------------------------------------------------

    @DeleteMapping("/{notificationId}")
    @Operation(
            summary = "Delete a notification",
            description = "Permanently deletes a notification. The authenticated user must own the notification."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notification deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            @AuthenticationPrincipal User currentUser,
            @Parameter(description = "UUID of the notification to delete")
            @PathVariable UUID notificationId
    ) {
        notificationService.deleteNotification(currentUser, notificationId);
        return ResponseEntity.ok(ApiResponse.message("Notification deleted successfully"));
    }
}
