package com.schemaforge.notification.repository;

import com.schemaforge.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    /** All notifications for a user, newest first. */
    List<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId);

    /** Only unread notifications for a user, newest first. */
    List<Notification> findByUserIdAndReadFalseOrderByCreatedAtDesc(UUID userId);

    /** Find a single notification for a specific user (for ownership checks). */
    Optional<Notification> findByIdAndUserId(UUID id, UUID userId);

    /** Count unread notifications for a user. */
    long countByUserIdAndReadFalse(UUID userId);

    /** Mark all notifications as read for a user in a single update. */
    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.userId = :userId AND n.read = false")
    int markAllReadByUserId(@Param("userId") UUID userId);
}
