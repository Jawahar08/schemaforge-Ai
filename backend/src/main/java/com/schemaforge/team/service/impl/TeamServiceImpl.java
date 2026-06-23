package com.schemaforge.team.service.impl;

import com.schemaforge.team.dto.CreateTeamRequest;
import com.schemaforge.team.dto.TeamResponse;
import com.schemaforge.team.dto.TeamSummaryResponse;
import com.schemaforge.team.dto.UpdateTeamRequest;
import com.schemaforge.team.entity.Team;
import com.schemaforge.team.entity.TeamMember;
import com.schemaforge.team.entity.TeamPlan;
import com.schemaforge.team.entity.TeamRole;
import com.schemaforge.team.mapper.TeamMapper;
import com.schemaforge.team.repository.TeamMemberRepository;
import com.schemaforge.team.repository.TeamRepository;
import com.schemaforge.team.service.TeamAuthHelper;
import com.schemaforge.team.service.TeamService;
import com.schemaforge.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamMapper teamMapper;
    private final TeamAuthHelper teamAuthHelper;

    @Override
    @Transactional
    public TeamResponse createTeam(User currentUser, CreateTeamRequest request) {
        String slug = generateUniqueSlug(request.name());

        Team team = Team.builder()
                .name(request.name())
                .slug(slug)
                .description(request.description())
                .owner(currentUser)
                .plan(TeamPlan.FREE)
                .build();

        Team savedTeam = teamRepository.save(team);

        // Add creator as TEAM_OWNER in membership table
        TeamMember ownerMembership = TeamMember.builder()
                .team(savedTeam)
                .user(currentUser)
                .role(TeamRole.OWNER)
                .joinedAt(Instant.now())
                .build();

        teamMemberRepository.save(ownerMembership);

        log.info("Team created: {} with slug: {} by user: {}", savedTeam.getId(), slug, currentUser.getId());
        return teamMapper.toResponse(savedTeam);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamSummaryResponse> getTeams(User currentUser) {
        return teamRepository.findAllForUser(currentUser.getId())
                .stream()
                .map(teamMapper::toSummaryResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TeamResponse getTeamById(User currentUser, UUID teamId) {
        teamAuthHelper.verifyTeamAccess(currentUser, teamId);
        Team team = teamAuthHelper.verifyTeamExists(teamId);
        return teamMapper.toResponse(team);
    }

    @Override
    @Transactional
    public TeamResponse updateTeam(User currentUser, UUID teamId, UpdateTeamRequest request) {
        teamAuthHelper.verifyTeamOwner(currentUser, teamId);
        Team team = teamAuthHelper.verifyTeamExists(teamId);

        if (request.name() != null) {
            team.setName(request.name());
            team.setSlug(generateUniqueSlug(request.name()));
        }
        if (request.description() != null) {
            team.setDescription(request.description());
        }

        Team saved = teamRepository.save(team);
        log.info("Team updated: {} by owner: {}", teamId, currentUser.getId());
        return teamMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteTeam(User currentUser, UUID teamId) {
        teamAuthHelper.verifyTeamOwner(currentUser, teamId);
        Team team = teamAuthHelper.verifyTeamExists(teamId);

        teamRepository.delete(team);
        log.info("Team deleted: {} by owner: {}", teamId, currentUser.getId());
    }

    private String generateUniqueSlug(String name) {
        String base = name.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        if (base.isEmpty()) {
            base = "team";
        }
        return base + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
