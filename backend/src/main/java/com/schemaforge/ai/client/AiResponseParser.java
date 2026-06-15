package com.schemaforge.ai.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AiResponseParser {

    private final ObjectMapper objectMapper;

    public AiResponseParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> parseObject(String rawText) {
        try {
            String cleaned = stripCodeFences(rawText);
            return objectMapper.readValue(cleaned, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception ex) {
            throw new IllegalArgumentException("Unable to parse AI response as JSON object: " + ex.getMessage(), ex);
        }
    }

    public List<Map<String, Object>> parseArray(String rawText) {
        try {
            String cleaned = stripCodeFences(rawText);
            return objectMapper.readValue(cleaned, new TypeReference<List<Map<String, Object>>>() {
            });
        } catch (Exception ex) {
            throw new IllegalArgumentException("Unable to parse AI response as JSON array: " + ex.getMessage(), ex);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> extractList(Map<String, Object> parent, String key) {
        Object value = parent.get(key);
        if (value instanceof List<?> list) {
            return (List<Map<String, Object>>) list;
        }
        return List.of();
    }

    private String stripCodeFences(String rawText) {
        String trimmed = rawText.trim();
        if (trimmed.startsWith("```")) {
            int firstNewline = trimmed.indexOf('\n');
            int lastFence = trimmed.lastIndexOf("```");
            if (firstNewline != -1 && lastFence > firstNewline) {
                trimmed = trimmed.substring(firstNewline + 1, lastFence).trim();
            }
        }
        return trimmed;
    }
}