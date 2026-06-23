package com.schemaforge.team.service;

import com.schemaforge.team.dto.TeamMemberResponse;
import com.schemaforge.user.entity.User;

import java.util.List;
import java.util.UUID;

public interface TeamMemberService {

    List<TeamMemberResponse> getTeamMembers(User currentUser, UUID teamId);

    void removeTeamMember(User currentUser, UUID teamId, UUID memberId);
}
