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
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final SchemaRepository schemaRepository;
    private final CommentMapper commentMapper;

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
