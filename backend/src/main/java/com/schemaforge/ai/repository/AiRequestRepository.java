package com.schemaforge.ai.repository;

import com.schemaforge.ai.entity.AiRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface AiRequestRepository extends JpaRepository<AiRequest, UUID> {

    @Query("SELECT r FROM AiRequest r WHERE r.user.id = :userId ORDER BY r.createdAt DESC")
    Page<AiRequest> findAllByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT r FROM AiRequest r WHERE r.id = :id AND r.user.id = :userId")
    Optional<AiRequest> findByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);

    @Query("SELECT COUNT(r) FROM AiRequest r WHERE r.user.id = :userId AND r.createdAt >= :since")
    long countByUserIdSince(@Param("userId") UUID userId, @Param("since") Instant since);
}