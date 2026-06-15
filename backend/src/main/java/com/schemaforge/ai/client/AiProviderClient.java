package com.schemaforge.ai.client;

import com.schemaforge.ai.entity.AiProvider;

import java.util.List;
import java.util.Map;

public interface AiProviderClient {

    AiProvider getProviderType();

    /**
     * Generates a complete schema (tables, relationships, normalization notes,
     * analysis items) from a natural-language system description.
     */
    AiSchemaGenerationResult generateSchema(String systemDescription, String normalizationTarget);

    /**
     * Generates normalization analysis (1NF-BCNF) for an existing set of tables.
     */
    List<Map<String, Object>> generateNormalization(List<Map<String, Object>> tables, String normalizationTarget);

    /**
     * Generates schema review / analysis items for an existing set of tables and relationships.
     */
    List<Map<String, Object>> generateAnalysis(List<Map<String, Object>> tables, List<Map<String, Object>> relationships);
}