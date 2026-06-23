package com.schemaforge.team.service;

import com.schemaforge.team.entity.Team;
import com.schemaforge.team.entity.TeamMember;
import com.schemaforge.team.entity.TeamRole;
import com.schemaforge.team.exception.TeamAccessDeniedException;
import com.schemaforge.team.exception.TeamNotFoundException;
import com.schemaforge.team.repository.TeamMemberRepository;
import com.schemaforge.team.repository.TeamRepository;
import com.schemaforge.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TeamAuthHelper {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    public Team verifyTeamExists(UUID teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException(teamId));
    }

    public void verifyTeamOwner(User user, UUID teamId) {
        Team team = verifyTeamExists(teamId);
        if (!team.getOwner().getId().equals(user.getId())) {
            throw new TeamAccessDeniedException("You do not have permission to manage this team. Only the team owner can perform this action.");
        }
    }

    public TeamMember verifyTeamMember(User user, UUID teamId) {
        verifyTeamExists(teamId);
        return teamMemberRepository.findByTeamIdAndUserId(teamId, user.getId())
                .orElseThrow(() -> new TeamAccessDeniedException("You do not have access to this team workspace. Membership is required."));
    }

    public void verifyTeamAccess(User user, UUID teamId) {
        Team team = verifyTeamExists(teamId);
        if (team.getOwner().getId().equals(user.getId())) {
            return;
        }
        boolean isMember = teamMemberRepository.existsByTeamIdAndUserId(teamId, user.getId());
        if (!isMember) {
            throw new TeamAccessDeniedException("You do not have access to this team workspace.");
        }
    }
}
