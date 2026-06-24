package com.schemaforge.comment.service.impl;

import com.schemaforge.comment.dto.CommentResponse;
import com.schemaforge.comment.dto.CreateCommentRequest;
import com.schemaforge.comment.dto.UpdateCommentRequest;
import com.schemaforge.comment.entity.Comment;
import com.schemaforge.comment.exception.CommentAccessDeniedException;
import com.schemaforge.comment.exception.CommentNotFoundException;
import com.schemaforge.comment.mapper.CommentMapper;
import com.schemaforge.comment.repository.CommentRepository;
import com.schemaforge.comment.service.CommentService;
import com.schemaforge.notification.entity.NotificationType;
import com.schemaforge.notification.service.NotificationService;
import com.schemaforge.schema.entity.Schema;
import com.schemaforge.schema.exception.SchemaNotFoundException;
import com.schemaforge.schema.repository.SchemaRepository;
import com.schemaforge.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final SchemaRepository schemaRepository;
    private final CommentMapper commentMapper;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public CommentResponse createComment(User currentUser, UUID schemaId, CreateCommentRequest request) {
        // verify schema exists and user has access to it
        Schema schema = schemaRepository.findActiveByIdAndOwnerId(schemaId, currentUser.getId())
                .orElseThrow(() -> new SchemaNotFoundException(schemaId));

        Comment comment = Comment.builder()
                .projectId(schema.getProject().getId())
                .schemaId(schema.getId())
                .userId(currentUser.getId())
                .parentCommentId(request.parentCommentId())
                .content(request.content())
                .entityReference(request.entityReference())
                .resolved(false)
                .edited(false)
                .build();

        Comment saved = commentRepository.save(comment);
        log.info("Comment created: {} on schema: {} by user: {}", saved.getId(), schemaId, currentUser.getId());

        // Notify the schema owner if the commenter is a different user.
        // schema.getProject().getOwner() gives us the project owner; for simplicity
        // we notify the commenter's schema owner (stored on Schema via Project).
        try {
            UUID schemaOwnerId = schema.getProject().getOwner().getId();
            if (!schemaOwnerId.equals(currentUser.getId())) {
                notificationService.createNotification(
                        schemaOwnerId,
                        NotificationType.COMMENT_ADDED,
                        "New comment on your schema",
                        currentUser.getFullName() + " commented on schema \"" + schema.getSystemName() + "\"",
                        Map.of(
                                "schemaId", schemaId.toString(),
                                "projectId", schema.getProject().getId().toString(),
                                "commentId", saved.getId().toString()
                        )
                );
            }
        } catch (Exception ex) {
            log.warn("Failed to create COMMENT_ADDED notification for schema {}: {}", schemaId, ex.getMessage());
        }

        return commentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(User currentUser, UUID schemaId) {
        // verify schema exists and user has access to it
        schemaRepository.findActiveByIdAndOwnerId(schemaId, currentUser.getId())
                .orElseThrow(() -> new SchemaNotFoundException(schemaId));

        return commentRepository.findBySchemaIdOrderByCreatedAtAsc(schemaId)
                .stream()
                .map(commentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CommentResponse updateComment(User currentUser, UUID commentId, UpdateCommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        // check edit permissions: users can edit only their own comments
        if (!comment.getUserId().equals(currentUser.getId())) {
            throw new CommentAccessDeniedException();
        }

        comment.setContent(request.content());
        comment.setEdited(true);
        comment.setEditedAt(Instant.now());

        Comment saved = commentRepository.save(comment);
        log.info("Comment updated: {} by user: {}", commentId, currentUser.getId());
        return commentMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteComment(User currentUser, UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        // check delete permissions: users can delete only their own comments
        if (!comment.getUserId().equals(currentUser.getId())) {
            throw new CommentAccessDeniedException();
        }

        commentRepository.delete(comment);
        log.info("Comment deleted: {} by user: {}", commentId, currentUser.getId());
    }
}
