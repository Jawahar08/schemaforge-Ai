package com.schemaforge.ai.mapper;

import com.schemaforge.ai.dto.AiRequestResponse;
import com.schemaforge.ai.dto.AiRequestSummaryResponse;
import com.schemaforge.ai.entity.AiRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AiRequestMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "schemaId", source = "schema.id")
    AiRequestResponse toResponse(AiRequest aiRequest);

    @Mapping(target = "schemaId", source = "schema.id")
    AiRequestSummaryResponse toSummaryResponse(AiRequest aiRequest);
}