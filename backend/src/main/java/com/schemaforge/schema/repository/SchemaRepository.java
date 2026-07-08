package com.schemaforge.schema.repository;

import com.schemaforge.schema.entity.Schema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SchemaRepository extends JpaRepository<Schema, UUID> {

    @Query("SELECT s FROM Schema s WHERE s.project.id = :projectId AND s.deletedAt IS NULL ORDER BY s.updatedAt DESC")
    List<Schema> findAllActiveByProjectId(@Param("projectId") UUID projectId);

    @Query("SELECT s FROM Schema s WHERE s.id = :id AND s.deletedAt IS NULL")
    Optional<Schema> findActiveById(@Param("id") UUID id);

    @Query("SELECT s FROM Schema s WHERE s.id = :id AND s.project.owner.id = :ownerId AND s.deletedAt IS NULL")
    Optional<Schema> findActiveByIdAndOwnerId(@Param("id") UUID id, @Param("ownerId") UUID ownerId);

    // Add these methods to your existing SchemaRepository interface

@Query("""
        SELECT COUNT(s) FROM Schema s
        WHERE s.project.owner.id = :ownerId
          AND s.deletedAt IS NULL
        """)
long countByProjectOwnerIdAndDeletedAtIsNull(@Param("ownerId") UUID ownerId);

@Query("""
        SELECT s FROM Schema s
        WHERE s.project.owner.id = :ownerId
          AND s.deletedAt IS NULL
        ORDER BY s.updatedAt DESC
        """)
List<Schema> findRecentByOwnerIdActive(@Param("ownerId") UUID ownerId, Pageable pageable);
}