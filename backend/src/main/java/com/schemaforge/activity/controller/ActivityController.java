package com.schemaforge.activity.controller;

import com.schemaforge.activity.dto.ActivityFilterRequest;
import com.schemaforge.activity.dto.ActivityResponse;
import com.schemaforge.activity.dto.ActivitySummaryResponse;
import com.schemaforge.activity.service.ActivityService;
import com.schemaforge.common.dto.ApiResponse;
import com.schemaforge.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(
        name = "Activity Feed",
        description = "Immutable activity history for users, projects, teams, and schemas"
)
public class ActivityController {

    private static final int MAX_PAGE_SIZE = 100;
    private static final int DEFAULT_PAGE_SIZE = 20;

    private final ActivityService activityService;

    @GetMapping("/api/activities")
    @Operation(
            summary = "Get my activity feed",
            description = "Returns the authenticated user's own activity history, newest first."
    )
    public ResponseEntity<ApiResponse<Page<ActivitySummaryResponse>>> getMyActivities(
            @Parameter(hidden = true)
            @AuthenticationPrincipal User currentUser,

            @ParameterObject
            ActivityFilterRequest filter,

            @ParameterObject
            @PageableDefault(
                    size = DEFAULT_PAGE_SIZE,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        activityService.getMyActivities(
                                currentUser,
                                filter,
                                clamp(pageable)
                        )
                )
        );
    }

    @GetMapping("/api/projects/{projectId}/activities")
    @Operation(
            summary = "Get project activity",
            description = "Returns activity for a project. Caller must own or be a team member."
    )
    public ResponseEntity<ApiResponse<Page<ActivitySummaryResponse>>> getProjectActivities(
            @Parameter(hidden = true)
            @AuthenticationPrincipal User currentUser,

            @PathVariable UUID projectId,

            @ParameterObject
            ActivityFilterRequest filter,

            @ParameterObject
            @PageableDefault(
                    size = DEFAULT_PAGE_SIZE,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        activityService.getProjectActivities(
                                currentUser,
                                projectId,
                                filter,
                                clamp(pageable)
                        )
                )
        );
    }

    @GetMapping("/api/teams/{teamId}/activities")
    @Operation(
            summary = "Get team activity",
            description = "Returns activity for a team. Caller must be an active member."
    )
    public ResponseEntity<ApiResponse<Page<ActivitySummaryResponse>>> getTeamActivities(
            @Parameter(hidden = true)
            @AuthenticationPrincipal User currentUser,

            @PathVariable UUID teamId,

            @ParameterObject
            ActivityFilterRequest filter,

            @ParameterObject
            @PageableDefault(
                    size = DEFAULT_PAGE_SIZE,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        activityService.getTeamActivities(
                                currentUser,
                                teamId,
                                filter,
                                clamp(pageable)
                        )
                )
        );
    }

    @GetMapping("/api/schemas/{schemaId}/activities")
    @Operation(
            summary = "Get schema activity",
            description = "Returns activity for a schema. Caller must own the parent project."
    )
    public ResponseEntity<ApiResponse<Page<ActivitySummaryResponse>>> getSchemaActivities(
            @Parameter(hidden = true)
            @AuthenticationPrincipal User currentUser,

            @PathVariable UUID schemaId,

            @ParameterObject
            @PageableDefault(
                    size = DEFAULT_PAGE_SIZE,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        activityService.getSchemaActivities(
                                currentUser,
                                schemaId,
                                clamp(pageable)
                        )
                )
        );
    }

    @GetMapping("/api/activities/{activityId}")
    @Operation(
            summary = "Get single activity record",
            description = "Returns a single activity. Caller must be the actor or have project/team access."
    )
    public ResponseEntity<ApiResponse<ActivityResponse>> getActivity(
            @Parameter(hidden = true)
            @AuthenticationPrincipal User currentUser,

            @PathVariable UUID activityId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        activityService.getActivityById(
                                currentUser,
                                activityId
                        )
                )
        );
    }

    private Pageable clamp(Pageable pageable) {
        if (pageable.getPageSize() <= MAX_PAGE_SIZE) {
            return pageable;
        }

        return PageRequest.of(
                pageable.getPageNumber(),
                MAX_PAGE_SIZE,
                pageable.getSort()
        );
    }
}