package com.schemaforge.project.repository;

import com.schemaforge.project.entity.Project;
import com.schemaforge.project.entity.ProjectStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    @Query("SELECT p FROM Project p WHERE p.owner.id = :ownerId AND p.deletedAt IS NULL ORDER BY p.updatedAt DESC")
    List<Project> findAllActiveByOwnerId(@Param("ownerId") UUID ownerId);

    @Query("SELECT p FROM Project p WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Project> findActiveById(@Param("id") UUID id);

    @Query("SELECT p FROM Project p WHERE p.id = :id AND p.owner.id = :ownerId AND p.deletedAt IS NULL")
    Optional<Project> findActiveByIdAndOwnerId(@Param("id") UUID id, @Param("ownerId") UUID ownerId);

    boolean existsByIdAndOwnerIdAndDeletedAtIsNull(UUID id, UUID ownerId);

    // Add these methods to your existing ProjectRepository interface

long countByOwnerIdAndDeletedAtIsNull(UUID ownerId);

long countByOwnerIdAndStatusAndDeletedAtIsNull(UUID ownerId, ProjectStatus status);



// findActiveById already exists from earlier work — verify it uses deletedAt IS NULL
}