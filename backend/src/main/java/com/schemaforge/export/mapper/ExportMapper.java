package com.schemaforge.export.mapper;

import com.schemaforge.export.dto.ExportResponse;
import com.schemaforge.export.entity.Export;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExportMapper {

    @Mapping(target = "exportId", source = "id")
    @Mapping(target = "schemaId", source = "schema.id")
    ExportResponse toResponse(Export export);
}