package com.schemaforge.ai.client;

import java.util.List;
import java.util.Map;

public record AiSchemaGenerationResult(
    String systemName,
    String description,
    List<Map<String, Object>> tables,
    List<Map<String, Object>> relationships,
    List<Map<String, Object>> normalizationNotes,
    List<Map<String, Object>> analysisItems,
    String rawResponse,
    String modelUsed,
    int promptTokens,
    int completionTokens,
    long latencyMs
) {
}