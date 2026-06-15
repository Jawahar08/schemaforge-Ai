package com.schemaforge.ai.service;

import com.schemaforge.ai.dto.GenerateSchemaRequest;
import com.schemaforge.schema.dto.SchemaResponse;
import com.schemaforge.user.entity.User;

public interface SchemaGenerationService {

    SchemaResponse generateSchema(User user, GenerateSchemaRequest request);
}