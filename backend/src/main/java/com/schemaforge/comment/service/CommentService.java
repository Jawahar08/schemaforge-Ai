package com.schemaforge.comment.service;

import com.schemaforge.comment.dto.CommentResponse;
import com.schemaforge.comment.dto.CreateCommentRequest;
import com.schemaforge.comment.dto.UpdateCommentRequest;
import com.schemaforge.user.entity.User;

import java.util.List;
import java.util.UUID;

public interface CommentService {

    CommentResponse createComment(User currentUser, UUID schemaId, CreateCommentRequest request);

    List<CommentResponse> getComments(User currentUser, UUID schemaId);

    CommentResponse updateComment(User currentUser, UUID commentId, UpdateCommentRequest request);

    void deleteComment(User currentUser, UUID commentId);
}
