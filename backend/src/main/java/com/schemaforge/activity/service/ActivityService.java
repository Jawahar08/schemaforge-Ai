package com.schemaforge.activity.service;

import com.schemaforge.activity.dto.ActivityFilterRequest;
import com.schemaforge.activity.dto.ActivityResponse;
import com.schemaforge.activity.dto.ActivitySummaryResponse;
import com.schemaforge.activity.dto.RecordActivityRequest;
import com.schemaforge.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ActivityService {

    Page<ActivitySummaryResponse> getMyActivities(
            User currentUser, ActivityFilterRequest filter, Pageable pageable);

    Page<ActivitySummaryResponse> getProjectActivities(
            User currentUser, UUID projectId, ActivityFilterRequest filter, Pageable pageable);

    Page<ActivitySummaryResponse> getTeamActivities(
            User currentUser, UUID teamId, ActivityFilterRequest filter, Pageable pageable);

    Page<ActivitySummaryResponse> getSchemaActivities(
            User currentUser, UUID schemaId, Pageable pageable);

    ActivityResponse getActivityById(User currentUser, UUID activityId);

    /**
     * Internal API — best-effort, runs in REQUIRES_NEW transaction.
     * A failure here must never roll back or break the primary operation.
     */
    void recordActivity(RecordActivityRequest request);
}