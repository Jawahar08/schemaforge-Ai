package com.schemaforge.team.mapper;

import com.schemaforge.team.dto.TeamMemberResponse;
import com.schemaforge.team.entity.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TeamMemberMapper {

    @Mapping(target = "teamId", source = "team.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "fullName", source = "user.fullName")
    TeamMemberResponse toResponse(TeamMember teamMember);
}
