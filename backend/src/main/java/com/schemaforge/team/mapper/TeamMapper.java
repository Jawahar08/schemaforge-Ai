package com.schemaforge.team.mapper;

import com.schemaforge.team.dto.TeamResponse;
import com.schemaforge.team.dto.TeamSummaryResponse;
import com.schemaforge.team.entity.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TeamMapper {

    @Mapping(target = "ownerId", source = "owner.id")
    TeamResponse toResponse(Team team);

    TeamSummaryResponse toSummaryResponse(Team team);
}
