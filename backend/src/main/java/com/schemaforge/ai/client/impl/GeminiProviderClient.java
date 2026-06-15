package com.schemaforge.ai.client.impl;

import com.schemaforge.ai.client.AiProviderClient;
import com.schemaforge.ai.client.AiResponseParser;
import com.schemaforge.ai.client.AiSchemaGenerationResult;
import com.schemaforge.ai.client.PromptBuilder;
import com.schemaforge.ai.entity.AiProvider;
import com.schemaforge.ai.exception.AiGenerationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GeminiProviderClient implements AiProviderClient {

    private final RestClient restClient;
    private final PromptBuilder promptBuilder;
    private final AiResponseParser responseParser;
    private final String apiKey;
    private final String model;

    public GeminiProviderClient(
            @Qualifier("geminiRestClient") RestClient restClient,
            PromptBuilder promptBuilder,
            AiResponseParser responseParser,
            @Value("${ai.gemini.api-key}") String apiKey,
            @Value("${ai.gemini.model}") String model
    ) {
        this.restClient = restClient;
        this.promptBuilder = promptBuilder;
        this.responseParser = responseParser;
        this.apiKey = apiKey;
        this.model = model;
    }

    @Override
    public AiProvider getProviderType() {
        return AiProvider.GEMINI;
    }

    @Override
    public AiSchemaGenerationResult generateSchema(String systemDescription, String normalizationTarget) {
        String prompt = promptBuilder.buildSchemaGenerationPrompt(systemDescription, normalizationTarget);
        long start = System.currentTimeMillis();

        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))),
                "generationConfig", Map.of("responseMimeType", "application/json")
        );

        try {
            Map<String, Object> rawResponse = callGenerateContent(body);
            long latency = System.currentTimeMillis() - start;

            String text = extractTextContent(rawResponse);
            Map<String, Object> parsed = responseParser.parseObject(text);

            @SuppressWarnings("unchecked")
            Map<String, Object> usage = (Map<String, Object>) rawResponse.getOrDefault("usageMetadata", Map.of());
            int promptTokens = ((Number) usage.getOrDefault("promptTokenCount", 0)).intValue();
            int completionTokens = ((Number) usage.getOrDefault("candidatesTokenCount", 0)).intValue();

            return new AiSchemaGenerationResult(
                    (String) parsed.get("systemName"),
                    (String) parsed.get("description"),
                    responseParser.extractList(parsed, "tables"),
                    responseParser.extractList(parsed, "relationships"),
                    responseParser.extractList(parsed, "normalizationNotes"),
                    responseParser.extractList(parsed, "analysisItems"),
                    text,
                    model,
                    promptTokens,
                    completionTokens,
                    latency
            );
        } catch (Exception ex) {
            log.error("Gemini schema generation failed: {}", ex.getMessage(), ex);
            throw new AiGenerationException("Gemini provider failed to generate schema: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<Map<String, Object>> generateNormalization(List<Map<String, Object>> tables, String normalizationTarget) {
        String prompt = promptBuilder.buildNormalizationPrompt(tables, normalizationTarget);

        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))),
                "generationConfig", Map.of("responseMimeType", "application/json")
        );

        try {
            Map<String, Object> rawResponse = callGenerateContent(body);
            String text = extractTextContent(rawResponse);
            return responseParser.parseArray(text);
        } catch (Exception ex) {
            log.error("Gemini normalization analysis failed: {}", ex.getMessage(), ex);
            throw new AiGenerationException("Gemini provider failed to generate normalization analysis: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<Map<String, Object>> generateAnalysis(List<Map<String, Object>> tables, List<Map<String, Object>> relationships) {
        String prompt = promptBuilder.buildAnalysisPrompt(tables, relationships);

        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))),
                "generationConfig", Map.of("responseMimeType", "application/json")
        );

        try {
            Map<String, Object> rawResponse = callGenerateContent(body);
            String text = extractTextContent(rawResponse);
            return responseParser.parseArray(text);
        } catch (Exception ex) {
            log.error("Gemini analysis generation failed: {}", ex.getMessage(), ex);
            throw new AiGenerationException("Gemini provider failed to generate analysis: " + ex.getMessage(), ex);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> callGenerateContent(Map<String, Object> body) {
        return restClient.post()
                .uri("/v1beta/models/{model}:generateContent?key={apiKey}", model, apiKey)
                .body(body)
                .retrieve()
                .body(Map.class);
    }

    @SuppressWarnings("unchecked")
    private String extractTextContent(Map<String, Object> rawResponse) {
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) rawResponse.get("candidates");
        if (candidates == null || candidates.isEmpty()) {
            throw new IllegalStateException("Gemini response contained no candidates");
        }
        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        return (String) parts.get(0).get("text");
    }
}