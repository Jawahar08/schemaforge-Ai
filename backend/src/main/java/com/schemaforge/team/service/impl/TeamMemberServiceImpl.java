package com.schemaforge.team.service.impl;

import com.schemaforge.common.exception.BadRequestException;
import com.schemaforge.team.dto.TeamMemberResponse;
import com.schemaforge.team.entity.Team;
import com.schemaforge.team.entity.TeamMember;
import com.schemaforge.team.exception.TeamAccessDeniedException;
import com.schemaforge.team.exception.TeamMemberNotFoundException;
import com.schemaforge.team.mapper.TeamMemberMapper;
import com.schemaforge.team.repository.TeamMemberRepository;
import com.schemaforge.team.service.TeamAuthHelper;
import com.schemaforge.team.service.TeamMemberService;
import com.schemaforge.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamMemberServiceImpl implements TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamMemberMapper teamMemberMapper;
    private final TeamAuthHelper teamAuthHelper;

    @Override
    @Transactional(readOnly = true)
    public List<TeamMemberResponse> getTeamMembers(User currentUser, UUID teamId) {
        teamAuthHelper.verifyTeamAccess(currentUser, teamId);

        return teamMemberRepository.findByTeamId(teamId)
                .stream()
                .map(teamMemberMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void removeTeamMember(User currentUser, UUID teamId, UUID memberId) {
        teamAuthHelper.verifyTeamOwner(currentUser, teamId);
        Team team = teamAuthHelper.verifyTeamExists(teamId);

        TeamMember member = teamMemberRepository.findById(memberId)
                .orElseThrow(() -> new TeamMemberNotFoundException(memberId));

        // Ensure the membership belongs to this team
        if (!member.getTeam().getId().equals(teamId)) {
            throw new TeamAccessDeniedException("Member does not belong to this team workspace");
        }

        // Rule: owner cannot remove himself
        if (member.getUser().getId().equals(team.getOwner().getId())) {
            throw new BadRequestException("The team owner cannot be removed from the team workspace");
        }

        teamMemberRepository.delete(member);
        log.info("Removed member: {} from team: {} by owner: {}", memberId, teamId, currentUser.getId());
    }
}
