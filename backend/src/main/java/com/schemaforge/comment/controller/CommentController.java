package com.schemaforge.comment.controller;

import com.schemaforge.comment.dto.CommentResponse;
import com.schemaforge.comment.dto.CreateCommentRequest;
import com.schemaforge.comment.dto.UpdateCommentRequest;
import com.schemaforge.comment.service.CommentService;
import com.schemaforge.common.dto.ApiResponse;
import com.schemaforge.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Comments", description = "Endpoints for managing schema collaboration comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/schemas/{schemaId}/comments")
    @Operation(summary = "Create a comment", description = "Adds a collaboration comment to a schema")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID schemaId,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        CommentResponse response = commentService.createComment(currentUser, schemaId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Comment created successfully", response));
    }

    @GetMapping("/api/schemas/{schemaId}/comments")
    @Operation(summary = "Get comments", description = "Retrieves all collaboration comments for a schema in chronological order")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID schemaId
    ) {
        List<CommentResponse> response = commentService.getComments(currentUser, schemaId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/api/comments/{commentId}")
    @Operation(summary = "Update a comment", description = "Edits the content of an existing comment owned by the user")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID commentId,
            @Valid @RequestBody UpdateCommentRequest request
    ) {
        CommentResponse response = commentService.updateComment(currentUser, commentId, request);
        return ResponseEntity.ok(ApiResponse.success("Comment updated successfully", response));
    }

    @DeleteMapping("/api/comments/{commentId}")
    @Operation(summary = "Delete a comment", description = "Deletes an existing comment owned by the user")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID commentId
    ) {
        commentService.deleteComment(currentUser, commentId);
        return ResponseEntity.ok(ApiResponse.message("Comment deleted successfully"));
    }
}
