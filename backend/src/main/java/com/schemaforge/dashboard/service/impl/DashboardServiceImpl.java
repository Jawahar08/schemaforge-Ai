package com.schemaforge.dashboard.service.impl;

import com.schemaforge.dashboard.dto.DashboardSummaryResponse;
import com.schemaforge.dashboard.dto.RecentProjectResponse;
import com.schemaforge.dashboard.dto.RecentSchemaResponse;
import com.schemaforge.dashboard.service.DashboardService;
import com.schemaforge.notification.repository.NotificationRepository;
import com.schemaforge.project.entity.ProjectStatus;
import com.schemaforge.project.repository.ProjectRepository;
import com.schemaforge.schema.repository.SchemaRepository;
import com.schemaforge.team.repository.TeamMemberRepository;
import com.schemaforge.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ProjectRepository projectRepository;
    private final SchemaRepository schemaRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final NotificationRepository notificationRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary(User currentUser) {
        var userId = currentUser.getId();

        long totalProjects   = projectRepository.countByOwnerIdAndDeletedAtIsNull(userId);
        long activeProjects  = projectRepository
                .countByOwnerIdAndStatusAndDeletedAtIsNull(userId, ProjectStatus.ACTIVE);
        long archivedProjects = projectRepository
                .countByOwnerIdAndStatusAndDeletedAtIsNull(userId, ProjectStatus.ARCHIVED);

        long totalSchemas = schemaRepository.countByProjectOwnerIdAndDeletedAtIsNull(userId);

        long totalTeams = teamMemberRepository.countByUserId(userId);

        long unreadNotifications = notificationRepository.countByUserIdAndReadFalse(userId);

        List<RecentProjectResponse> recentProjects = projectRepository
                .findAllActiveByOwnerId(userId)
                .stream()
                .limit(5)
                .map(p -> new RecentProjectResponse(
                        p.getId(),
                        p.getName(),
                        p.getDescription(),
                        p.getDialect(),
                        p.getStatus(),
                        p.getTags(),
                        p.getUpdatedAt()
                ))
                .toList();

        var schemaPage = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "updatedAt"));
        List<RecentSchemaResponse> recentSchemas = schemaRepository
                .findRecentByOwnerIdActive(userId, schemaPage)
                .stream()
                .map(s -> new RecentSchemaResponse(
                        s.getId(),
                        s.getProject().getId(),
                        s.getProject().getName(),
                        s.getSystemName(),
                        s.getDescription(),
                        s.getNormalizationTarget(),
                        s.getStatus(),
                        s.getCurrentVersion(),
                        s.getTables() != null ? s.getTables().size() : 0,
                        s.getUpdatedAt()
                ))
                .toList();

        return new DashboardSummaryResponse(
                totalProjects,
                activeProjects,
                archivedProjects,
                totalSchemas,
                totalTeams,
                unreadNotifications,
                recentProjects,
                recentSchemas
        );
    }
}