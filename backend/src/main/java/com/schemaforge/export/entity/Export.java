package com.schemaforge.export.entity;

import com.schemaforge.common.entity.CreatedOnlyEntity;
import com.schemaforge.schema.entity.Schema;
import com.schemaforge.schema.entity.SchemaVersion;
import com.schemaforge.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "exports")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Export extends CreatedOnlyEntity {

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schema_id", nullable = false)
    private Schema schema;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schema_version_id")
    private SchemaVersion schemaVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by_id", nullable = false)
    private User requestedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "export_type", nullable = false, length = 20)
    private ExportType exportType;

    @Convert(converter = ExportDialectConverter.class)
    @Column(name = "dialect", length = 20)
    private ExportDialect dialect;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ExportStatus status = ExportStatus.PENDING;

    // Stores the generated SQL script text.
    // Column is VARCHAR(1024) in the migration; for longer scripts this should
    // be migrated to TEXT in a future V13 migration. For now content is stored
    // here and returned directly on the download endpoint.
    @Column(name = "file_url", length = 1024)
    private String content;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(name = "completed_at")
    private Instant completedAt;
}