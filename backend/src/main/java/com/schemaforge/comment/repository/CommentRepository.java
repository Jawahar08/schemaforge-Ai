package com.schemaforge.comment.repository;

import com.schemaforge.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    List<Comment> findBySchemaIdOrderByCreatedAtAsc(UUID schemaId);

    Optional<Comment> findByIdAndUserId(UUID id, UUID userId);

    long countBySchemaId(UUID schemaId);
}
