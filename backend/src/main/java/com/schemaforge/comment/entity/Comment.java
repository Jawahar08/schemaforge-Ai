package com.schemaforge.comment.entity;

import com.schemaforge.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "comments")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment extends BaseEntity {

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "schema_id")
    private UUID schemaId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "parent_comment_id")
    private UUID parentCommentId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "entity_reference", length = 255)
    private String entityReference;

    @Column(name = "resolved", nullable = false)
    @Builder.Default
    private boolean resolved = false;

    @Column(name = "edited", nullable = false)
    @Builder.Default
    private boolean edited = false;

    @Column(name = "edited_at")
    private Instant editedAt;
}
