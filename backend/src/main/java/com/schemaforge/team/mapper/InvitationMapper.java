package com.schemaforge.team.mapper;

import com.schemaforge.team.dto.InvitationResponse;
import com.schemaforge.team.entity.Invitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface InvitationMapper {

    @Mapping(target = "teamId", source = "team.id")
    @Mapping(target = "invitedById", source = "invitedBy.id")
    InvitationResponse toResponse(Invitation invitation);
}
