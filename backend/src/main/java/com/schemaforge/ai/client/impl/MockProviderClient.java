package com.schemaforge.ai.client.impl;

import com.schemaforge.ai.client.AiProviderClient;
import com.schemaforge.ai.client.AiSchemaGenerationResult;
import com.schemaforge.ai.entity.AiProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class MockProviderClient implements AiProviderClient {
@Override
public AiProvider getProviderType() {
    return AiProvider.MOCK;
}

@Override
public AiSchemaGenerationResult generateSchema(
        String description,
        String normalizationTarget
) {

    return new AiSchemaGenerationResult(
            "Library Management System",
            "Mock generated schema for testing",

            List.of(
                    Map.of(
                            "name", "users",
                            "fields", List.of(
                                    Map.of("name", "id", "type", "UUID"),
                                    Map.of("name", "name", "type", "VARCHAR")
                            )
                    ),
                    Map.of(
                            "name", "books",
                            "fields", List.of(
                                    Map.of("name", "id", "type", "UUID"),
                                    Map.of("name", "title", "type", "VARCHAR")
                            )
                    )
            ),

            List.of(),

            List.of(
                    Map.of(
                            "level", "INFO",
                            "message", "Schema normalized to 3NF"
                    )
            ),

            List.of(
                    Map.of(
                            "severity", "INFO",
                            "message", "Mock schema generated successfully"
                    )
            ),

            "{\"mock\":true}",
            "MOCK",
            100,
            50,
            200L
    );
}

@Override
public List<Map<String, Object>> generateNormalization(
        List<Map<String, Object>> tables,
        String normalizationTarget
) {
    return List.of(
            Map.of(
                    "level", "INFO",
                    "message", "Mock normalization completed"
            )
    );
}

@Override
public List<Map<String, Object>> generateAnalysis(
        List<Map<String, Object>> tables,
        List<Map<String, Object>> relationships
) {
    return List.of(
            Map.of(
                    "severity", "INFO",
                    "message", "Mock analysis completed successfully"
            )
    );
}
}
