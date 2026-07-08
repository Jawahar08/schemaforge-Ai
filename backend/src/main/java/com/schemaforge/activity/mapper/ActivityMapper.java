package com.schemaforge.activity.mapper;

import com.schemaforge.activity.dto.ActivityResponse;
import com.schemaforge.activity.dto.ActivitySummaryResponse;
import com.schemaforge.activity.entity.Activity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ActivityMapper {

    @Mapping(target = "actorUserId", source = "actor.id")
    @Mapping(target = "actorName", source = "actor.fullName")
    ActivityResponse toResponse(Activity activity);

    @Mapping(target = "actorUserId", source = "actor.id")
    @Mapping(target = "actorName", source = "actor.fullName")
    ActivitySummaryResponse toSummaryResponse(Activity activity);
}