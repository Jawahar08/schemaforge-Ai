package com.schemaforge.export.repository;

import com.schemaforge.export.entity.Export;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExportRepository extends JpaRepository<Export, UUID> {

    @Query("""
            SELECT e FROM Export e
            WHERE e.schema.id = :schemaId
              AND e.schema.project.owner.id = :ownerId
            ORDER BY e.createdAt DESC
            """)
    List<Export> findAllBySchemaIdAndOwnerId(
            @Param("schemaId") UUID schemaId,
            @Param("ownerId") UUID ownerId
    );

    @Query("""
            SELECT e FROM Export e
            WHERE e.id = :id
              AND e.schema.project.owner.id = :ownerId
            """)
    Optional<Export> findByIdAndOwnerId(
            @Param("id") UUID id,
            @Param("ownerId") UUID ownerId
    );
}