package com.schemaforge.activity.service.impl;

import com.schemaforge.activity.dto.ActivityFilterRequest;
import com.schemaforge.activity.dto.ActivityResponse;
import com.schemaforge.activity.dto.ActivitySummaryResponse;
import com.schemaforge.activity.dto.RecordActivityRequest;
import com.schemaforge.activity.entity.Activity;
import com.schemaforge.activity.exception.ActivityNotFoundException;
import com.schemaforge.activity.mapper.ActivityMapper;
import com.schemaforge.activity.repository.ActivityRepository;
import com.schemaforge.activity.service.ActivityService;
import com.schemaforge.common.exception.ForbiddenException;
import com.schemaforge.project.repository.ProjectRepository;
import com.schemaforge.schema.exception.SchemaNotFoundException;
import com.schemaforge.schema.repository.SchemaRepository;
import com.schemaforge.team.repository.TeamMemberRepository;
import com.schemaforge.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final ActivityMapper activityMapper;
    private final ProjectRepository projectRepository;
    private final SchemaRepository schemaRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ActivitySummaryResponse> getMyActivities(
            User currentUser,
            ActivityFilterRequest filter,
            Pageable pageable
    ) {
        ActivityFilterRequest f = normalizeFilter(filter);

        Page<Activity> activities;

        if (f.from() != null && f.to() != null) {
            activities = activityRepository.findByActorFilteredBetween(
                    currentUser.getId(),
                    f.activityType(),
                    f.from(),
                    f.to(),
                    pageable
            );
        } else if (f.from() != null) {
            activities = activityRepository.findByActorFilteredFrom(
                    currentUser.getId(),
                    f.activityType(),
                    f.from(),
                    pageable
            );
        } else if (f.to() != null) {
            activities = activityRepository.findByActorFilteredTo(
                    currentUser.getId(),
                    f.activityType(),
                    f.to(),
                    pageable
            );
        } else {
            activities = activityRepository.findByActorFiltered(
                    currentUser.getId(),
                    f.activityType(),
                    pageable
            );
        }

        return activities.map(activityMapper::toSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivitySummaryResponse> getProjectActivities(
            User currentUser,
            UUID projectId,
            ActivityFilterRequest filter,
            Pageable pageable
    ) {
        validateProjectAccess(currentUser, projectId);

        ActivityFilterRequest f = normalizeFilter(filter);

        Page<Activity> activities;

        if (f.from() != null && f.to() != null) {
            activities = activityRepository.findByProjectFilteredBetween(
                    projectId,
                    f.activityType(),
                    f.from(),
                    f.to(),
                    pageable
            );
        } else if (f.from() != null) {
            activities = activityRepository.findByProjectFilteredFrom(
                    projectId,
                    f.activityType(),
                    f.from(),
                    pageable
            );
        } else if (f.to() != null) {
            activities = activityRepository.findByProjectFilteredTo(
                    projectId,
                    f.activityType(),
                    f.to(),
                    pageable
            );
        } else {
            activities = activityRepository.findByProjectFiltered(
                    projectId,
                    f.activityType(),
                    pageable
            );
        }

        return activities.map(activityMapper::toSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivitySummaryResponse> getTeamActivities(
            User currentUser,
            UUID teamId,
            ActivityFilterRequest filter,
            Pageable pageable
    ) {
        if (!teamMemberRepository.existsByTeamIdAndUserId(
                teamId,
                currentUser.getId()
        )) {
            throw new ForbiddenException("You are not a member of this team");
        }

        ActivityFilterRequest f = normalizeFilter(filter);

        Page<Activity> activities;

        if (f.from() != null && f.to() != null) {
            activities = activityRepository.findByTeamFilteredBetween(
                    teamId,
                    f.activityType(),
                    f.from(),
                    f.to(),
                    pageable
            );
        } else if (f.from() != null) {
            activities = activityRepository.findByTeamFilteredFrom(
                    teamId,
                    f.activityType(),
                    f.from(),
                    pageable
            );
        } else if (f.to() != null) {
            activities = activityRepository.findByTeamFilteredTo(
                    teamId,
                    f.activityType(),
                    f.to(),
                    pageable
            );
        } else {
            activities = activityRepository.findByTeamFiltered(
                    teamId,
                    f.activityType(),
                    pageable
            );
        }

        return activities.map(activityMapper::toSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivitySummaryResponse> getSchemaActivities(
            User currentUser,
            UUID schemaId,
            Pageable pageable
    ) {
        var schema = schemaRepository.findActiveById(schemaId)
                .orElseThrow(() -> new SchemaNotFoundException(schemaId));

        validateProjectAccess(
                currentUser,
                schema.getProject().getId()
        );

        return activityRepository.findBySchemaId(schemaId, pageable)
                .map(activityMapper::toSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ActivityResponse getActivityById(
            User currentUser,
            UUID activityId
    ) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));

        boolean isActor =
                activity.getActor() != null
                        && activity.getActor().getId().equals(currentUser.getId());

        boolean hasProjectAccess =
                activity.getProjectId() != null
                        && projectRepository.existsByIdAndOwnerIdAndDeletedAtIsNull(
                                activity.getProjectId(),
                                currentUser.getId()
                        );

        boolean hasTeamAccess =
                activity.getTeamId() != null
                        && teamMemberRepository.existsByTeamIdAndUserId(
                                activity.getTeamId(),
                                currentUser.getId()
                        );

        if (!isActor && !hasProjectAccess && !hasTeamAccess) {
            throw new ForbiddenException(
                    "You do not have access to this activity record"
            );
        }

        return activityMapper.toResponse(activity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordActivity(RecordActivityRequest request) {
        try {
            Activity activity = Activity.builder()
                    .actor(request.actor())
                    .projectId(request.projectId())
                    .teamId(request.teamId())
                    .schemaId(request.schemaId())
                    .activityType(request.activityType())
                    .entityType(request.entityType())
                    .entityId(request.entityId())
                    .title(request.title())
                    .description(request.description())
                    .metadata(
                            request.metadata() != null
                                    ? request.metadata()
                                    : Collections.emptyMap()
                    )
                    .build();

            activityRepository.save(activity);

            log.debug(
                    "Activity recorded: type={} entity={} actor={}",
                    request.activityType(),
                    request.entityId(),
                    request.actor() != null
                            ? request.actor().getId()
                            : "SYSTEM"
            );

        } catch (Exception ex) {
            log.warn(
                    "Failed to record activity type={}: {}",
                    request.activityType(),
                    ex.getMessage()
            );
        }
    }

    private ActivityFilterRequest normalizeFilter(
            ActivityFilterRequest filter
    ) {
        return filter != null
                ? filter
                : new ActivityFilterRequest(null, null, null);
    }

    private void validateProjectAccess(
            User user,
            UUID projectId
    ) {
        if (projectRepository.existsByIdAndOwnerIdAndDeletedAtIsNull(
                projectId,
                user.getId()
        )) {
            return;
        }

        boolean teamMember = projectRepository.findActiveById(projectId)
                .map(project ->
                        project.getTeamId() != null
                                && teamMemberRepository.existsByTeamIdAndUserId(
                                        project.getTeamId(),
                                        user.getId()
                                )
                )
                .orElse(false);

        if (!teamMember) {
            throw new ForbiddenException(
                    "You do not have access to this project's activity"
            );
        }
    }
}