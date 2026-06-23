package com.schemaforge.team.service;

import com.schemaforge.team.dto.CreateTeamRequest;
import com.schemaforge.team.dto.TeamResponse;
import com.schemaforge.team.dto.TeamSummaryResponse;
import com.schemaforge.team.dto.UpdateTeamRequest;
import com.schemaforge.user.entity.User;

import java.util.List;
import java.util.UUID;

public interface TeamService {

    TeamResponse createTeam(User currentUser, CreateTeamRequest request);

    List<TeamSummaryResponse> getTeams(User currentUser);

    TeamResponse getTeamById(User currentUser, UUID teamId);

    TeamResponse updateTeam(User currentUser, UUID teamId, UpdateTeamRequest request);

    void deleteTeam(User currentUser, UUID teamId);
}
