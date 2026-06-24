package com.schemaforge.notification.service.impl;

import com.schemaforge.notification.dto.NotificationResponse;
import com.schemaforge.notification.entity.Notification;
import com.schemaforge.notification.entity.NotificationType;
import com.schemaforge.notification.exception.NotificationNotFoundException;
import com.schemaforge.notification.mapper.NotificationMapper;
import com.schemaforge.notification.repository.NotificationRepository;
import com.schemaforge.notification.service.NotificationService;
import com.schemaforge.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications(User currentUser) {
        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications(User currentUser) {
        return notificationRepository
                .findByUserIdAndReadFalseOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(User currentUser, UUID notificationId) {
        Notification notification = notificationRepository
                .findByIdAndUserId(notificationId, currentUser.getId())
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));

        notification.setRead(true);
        Notification saved = notificationRepository.save(notification);

        log.debug("Notification {} marked as read for user {}", notificationId, currentUser.getId());
        return notificationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public int markAllAsRead(User currentUser) {
        int updated = notificationRepository.markAllReadByUserId(currentUser.getId());
        log.info("Marked {} notifications as read for user {}", updated, currentUser.getId());
        return updated;
    }

    @Override
    @Transactional
    public void deleteNotification(User currentUser, UUID notificationId) {
        Notification notification = notificationRepository
                .findByIdAndUserId(notificationId, currentUser.getId())
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));

        notificationRepository.delete(notification);
        log.info("Notification {} deleted by user {}", notificationId, currentUser.getId());
    }

    @Override
    @Transactional
    public void createNotification(
            UUID userId,
            NotificationType type,
            String title,
            String message,
            Map<String, String> metadata
    ) {
        Notification notification = Notification.builder()
                .userId(userId)
                .type(type)
                .title(title)
                .message(message)
                .metadata(metadata != null ? metadata : new java.util.HashMap<>())
                .read(false)
                .build();

        notificationRepository.save(notification);
        log.info("Notification created: type={} for userId={}", type, userId);
    }
}
