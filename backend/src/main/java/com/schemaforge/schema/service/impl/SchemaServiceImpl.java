package com.schemaforge.schema.service.impl;

import com.schemaforge.activity.dto.RecordActivityRequest;
import com.schemaforge.activity.entity.ActivityType;
import com.schemaforge.activity.service.ActivityService;
import com.schemaforge.project.entity.Project;
import com.schemaforge.project.exception.ProjectNotFoundException;
import com.schemaforge.project.repository.ProjectRepository;
import com.schemaforge.schema.dto.CreateSchemaRequest;
import com.schemaforge.schema.dto.SchemaResponse;
import com.schemaforge.schema.dto.SchemaSummaryResponse;
import com.schemaforge.schema.dto.SchemaVersionResponse;
import com.schemaforge.schema.dto.SchemaVersionSummaryResponse;
import com.schemaforge.schema.dto.UpdateSchemaRequest;
import com.schemaforge.schema.entity.Schema;
import com.schemaforge.schema.entity.SchemaVersion;
import com.schemaforge.schema.exception.SchemaNotFoundException;
import com.schemaforge.schema.exception.SchemaVersionNotFoundException;
import com.schemaforge.schema.mapper.SchemaMapper;
import com.schemaforge.schema.mapper.SchemaVersionMapper;
import com.schemaforge.schema.repository.SchemaRepository;
import com.schemaforge.schema.repository.SchemaVersionRepository;
import com.schemaforge.schema.service.SchemaService;
import com.schemaforge.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchemaServiceImpl implements SchemaService {

    private final SchemaRepository schemaRepository;
    private final SchemaVersionRepository schemaVersionRepository;
    private final ProjectRepository projectRepository;
    private final SchemaMapper schemaMapper;
    private final SchemaVersionMapper schemaVersionMapper;
    private final ActivityService activityService;

    @Override
    @Transactional
    public SchemaResponse createSchema(
            User owner,
            UUID projectId,
            CreateSchemaRequest request
    ) {
        Project project = projectRepository
                .findActiveByIdAndOwnerId(projectId, owner.getId())
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        Schema schema = Schema.builder()
                .project(project)
                .systemName(request.systemName())
                .description(request.description())
                .normalizationTarget(request.normalizationTarget())
                .tables(
                        request.tables() != null
                                ? request.tables()
                                : Collections.emptyList()
                )
                .relationships(
                        request.relationships() != null
                                ? request.relationships()
                                : Collections.emptyList()
                )
                .normalizationNotes(
                        request.normalizationNotes() != null
                                ? request.normalizationNotes()
                                : Collections.emptyList()
                )
                .analysisItems(
                        request.analysisItems() != null
                                ? request.analysisItems()
                                : Collections.emptyList()
                )
                .currentVersion(1)
                .build();

        Schema saved = schemaRepository.save(schema);

        createVersionSnapshot(
                saved,
                owner,
                "Initial schema generation"
        );

        try {
            activityService.recordActivity(
                    RecordActivityRequest.forSchema(
                            owner,
                            saved.getProject().getId(),
                            saved.getId(),
                            ActivityType.SCHEMA_CREATED,
                            saved.getId(),
                            "Schema \"" + saved.getSystemName() + "\" was created",
                            Map.of(
                                    "schemaName", saved.getSystemName(),
                                    "version", saved.getCurrentVersion()
                            )
                    )
            );
        } catch (Exception ex) {
            log.warn(
                    "Failed to record SCHEMA_CREATED activity for schema: {}",
                    saved.getId(),
                    ex
            );
        }

        log.info(
                "Schema created: {} for project: {}",
                saved.getId(),
                project.getId()
        );

        return schemaMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SchemaSummaryResponse> getSchemasForProject(
            User owner,
            UUID projectId
    ) {
        projectRepository
                .findActiveByIdAndOwnerId(projectId, owner.getId())
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        return schemaRepository
                .findAllActiveByProjectId(projectId)
                .stream()
                .map(schemaMapper::toSummaryResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SchemaResponse getSchemaById(
            User owner,
            UUID schemaId
    ) {
        Schema schema = schemaRepository
                .findActiveByIdAndOwnerId(schemaId, owner.getId())
                .orElseThrow(() -> new SchemaNotFoundException(schemaId));

        return schemaMapper.toResponse(schema);
    }

    @Override
    @Transactional
    public SchemaResponse updateSchema(
            User owner,
            UUID schemaId,
            UpdateSchemaRequest request
    ) {
        Schema schema = schemaRepository
                .findActiveByIdAndOwnerId(schemaId, owner.getId())
                .orElseThrow(() -> new SchemaNotFoundException(schemaId));

        if (request.systemName() != null) {
            schema.setSystemName(request.systemName());
        }

        if (request.description() != null) {
            schema.setDescription(request.description());
        }

        if (request.normalizationTarget() != null) {
            schema.setNormalizationTarget(request.normalizationTarget());
        }

        if (request.tables() != null) {
            schema.setTables(request.tables());
        }

        if (request.relationships() != null) {
            schema.setRelationships(request.relationships());
        }

        if (request.normalizationNotes() != null) {
            schema.setNormalizationNotes(request.normalizationNotes());
        }

        if (request.analysisItems() != null) {
            schema.setAnalysisItems(request.analysisItems());
        }

        if (request.status() != null) {
            schema.setStatus(request.status());
        }

        schema.setCurrentVersion(
                schema.getCurrentVersion() + 1
        );

        Schema saved = schemaRepository.save(schema);

        String summary =
                request.changeSummary() != null
                        && !request.changeSummary().isBlank()
                        ? request.changeSummary()
                        : "Schema updated";

        createVersionSnapshot(
                saved,
                owner,
                summary
        );

        try {
            activityService.recordActivity(
                    RecordActivityRequest.forSchema(
                            owner,
                            saved.getProject().getId(),
                            saved.getId(),
                            ActivityType.SCHEMA_UPDATED,
                            saved.getId(),
                            "Schema \"" + saved.getSystemName()
                                    + "\" was updated to version "
                                    + saved.getCurrentVersion(),
                            Map.of(
                                    "schemaName", saved.getSystemName(),
                                    "version", saved.getCurrentVersion()
                            )
                    )
            );
        } catch (Exception ex) {
            log.warn(
                    "Failed to record SCHEMA_UPDATED activity for schema: {}",
                    saved.getId(),
                    ex
            );
        }

        log.info(
                "Schema updated: {} (new version: {})",
                saved.getId(),
                saved.getCurrentVersion()
        );

        return schemaMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteSchema(
            User owner,
            UUID schemaId
    ) {
        Schema schema = schemaRepository
                .findActiveByIdAndOwnerId(schemaId, owner.getId())
                .orElseThrow(() -> new SchemaNotFoundException(schemaId));

        schema.setDeletedAt(Instant.now());
        schemaRepository.save(schema);

        log.info(
                "Schema soft-deleted: {}",
                schema.getId()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<SchemaVersionSummaryResponse> getVersionHistory(
            User owner,
            UUID schemaId
    ) {
        schemaRepository
                .findActiveByIdAndOwnerId(schemaId, owner.getId())
                .orElseThrow(() -> new SchemaNotFoundException(schemaId));

        return schemaVersionRepository
                .findAllBySchemaIdOrderByVersionDesc(schemaId)
                .stream()
                .map(schemaVersionMapper::toSummaryResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SchemaVersionResponse getVersion(
            User owner,
            UUID schemaId,
            Integer versionNumber
    ) {
        schemaRepository
                .findActiveByIdAndOwnerId(schemaId, owner.getId())
                .orElseThrow(() -> new SchemaNotFoundException(schemaId));

        SchemaVersion version = schemaVersionRepository
                .findBySchemaIdAndVersionNumber(
                        schemaId,
                        versionNumber
                )
                .orElseThrow(
                        () -> new SchemaVersionNotFoundException(
                                schemaId,
                                versionNumber
                        )
                );

        return schemaVersionMapper.toResponse(version);
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public SchemaResponse restoreVersion(
            User owner,
            UUID schemaId,
            Integer versionNumber
    ) {
        Schema schema = schemaRepository
                .findActiveByIdAndOwnerId(schemaId, owner.getId())
                .orElseThrow(() -> new SchemaNotFoundException(schemaId));

        SchemaVersion targetVersion = schemaVersionRepository
                .findBySchemaIdAndVersionNumber(
                        schemaId,
                        versionNumber
                )
                .orElseThrow(
                        () -> new SchemaVersionNotFoundException(
                                schemaId,
                                versionNumber
                        )
                );

        Map<String, Object> snapshot =
                targetVersion.getSnapshot();

        schema.setSystemName(
                (String) snapshot.getOrDefault(
                        "systemName",
                        schema.getSystemName()
                )
        );

        schema.setDescription(
                (String) snapshot.getOrDefault(
                        "description",
                        schema.getDescription()
                )
        );

        if (snapshot.get("tables") instanceof List<?> tables) {
            schema.setTables(
                    (List<Map<String, Object>>) tables
            );
        }

        if (snapshot.get("relationships")
                instanceof List<?> relationships) {
            schema.setRelationships(
                    (List<Map<String, Object>>) relationships
            );
        }

        if (snapshot.get("normalizationNotes")
                instanceof List<?> notes) {
            schema.setNormalizationNotes(
                    (List<Map<String, Object>>) notes
            );
        }

        if (snapshot.get("analysisItems")
                instanceof List<?> items) {
            schema.setAnalysisItems(
                    (List<Map<String, Object>>) items
            );
        }

        schema.setCurrentVersion(
                schema.getCurrentVersion() + 1
        );

        Schema saved =
                schemaRepository.save(schema);

        createVersionSnapshot(
                saved,
                owner,
                "Restored from version " + versionNumber
        );

        try {
            activityService.recordActivity(
                    RecordActivityRequest.forSchema(
                            owner,
                            saved.getProject().getId(),
                            saved.getId(),
                            ActivityType.SCHEMA_RESTORED,
                            saved.getId(),
                            "Schema \"" + saved.getSystemName()
                                    + "\" restored to version "
                                    + versionNumber,
                            Map.of(
                                    "schemaName", saved.getSystemName(),
                                    "restoredToVersion", versionNumber
                            )
                    )
            );
        } catch (Exception ex) {
            log.warn(
                    "Failed to record SCHEMA_RESTORED activity for schema: {}",
                    saved.getId(),
                    ex
            );
        }

        log.info(
                "Schema {} restored from version {} (new version: {})",
                schemaId,
                versionNumber,
                saved.getCurrentVersion()
        );

        return schemaMapper.toResponse(saved);
    }

    private void createVersionSnapshot(
            Schema schema,
            User createdBy,
            String changeSummary
    ) {
        Map<String, Object> snapshot =
                new LinkedHashMap<>();

        snapshot.put(
                "systemName",
                schema.getSystemName()
        );

        snapshot.put(
                "description",
                schema.getDescription()
        );

        snapshot.put(
                "normalizationTarget",
                schema.getNormalizationTarget().getValue()
        );

        snapshot.put(
                "tables",
                schema.getTables()
        );

        snapshot.put(
                "relationships",
                schema.getRelationships()
        );

        snapshot.put(
                "normalizationNotes",
                schema.getNormalizationNotes()
        );

        snapshot.put(
                "analysisItems",
                schema.getAnalysisItems()
        );

        snapshot.put(
                "status",
                schema.getStatus().name()
        );

        SchemaVersion version =
                SchemaVersion.builder()
                        .schema(schema)
                        .versionNumber(
                                schema.getCurrentVersion()
                        )
                        .snapshot(snapshot)
                        .changeSummary(changeSummary)
                        .createdBy(createdBy)
                        .build();

        schemaVersionRepository.save(version);
    }
}