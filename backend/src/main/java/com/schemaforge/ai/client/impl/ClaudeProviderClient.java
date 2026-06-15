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
public class ClaudeProviderClient implements AiProviderClient {

    private final RestClient restClient;
    private final PromptBuilder promptBuilder;
    private final AiResponseParser responseParser;
    private final String apiKey;
    private final String model;

    public ClaudeProviderClient(
            @Qualifier("claudeRestClient") RestClient restClient,
            PromptBuilder promptBuilder,
            AiResponseParser responseParser,
            @Value("${ai.claude.api-key}") String apiKey,
            @Value("${ai.claude.model}") String model
    ) {
        this.restClient = restClient;
        this.promptBuilder = promptBuilder;
        this.responseParser = responseParser;
        this.apiKey = apiKey;
        this.model = model;
    }

    @Override
    public AiProvider getProviderType() {
        return AiProvider.CLAUDE;
    }

    @Override
    public AiSchemaGenerationResult generateSchema(String systemDescription, String normalizationTarget) {
        String prompt = promptBuilder.buildSchemaGenerationPrompt(systemDescription, normalizationTarget);
        long start = System.currentTimeMillis();

        Map<String, Object> body = Map.of(
                "model", model,
                "max_tokens", 4000,
                "messages", List.of(Map.of("role", "user", "content", prompt))
        );

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> rawResponse = restClient.post()
                    .uri("/v1/messages")
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            long latency = System.currentTimeMillis() - start;
            String text = extractTextContent(rawResponse);
            Map<String, Object> parsed = responseParser.parseObject(text);

            Map<String, Object> usage = (Map<String, Object>) rawResponse.getOrDefault("usage", Map.of());
            int promptTokens = ((Number) usage.getOrDefault("input_tokens", 0)).intValue();
            int completionTokens = ((Number) usage.getOrDefault("output_tokens", 0)).intValue();

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
            log.error("Claude schema generation failed: {}", ex.getMessage(), ex);
            throw new AiGenerationException("Claude provider failed to generate schema: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<Map<String, Object>> generateNormalization(List<Map<String, Object>> tables, String normalizationTarget) {
        String prompt = promptBuilder.buildNormalizationPrompt(tables, normalizationTarget);

        Map<String, Object> body = Map.of(
                "model", model,
                "max_tokens", 2000,
                "messages", List.of(Map.of("role", "user", "content", prompt))
        );

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> rawResponse = restClient.post()
                    .uri("/v1/messages")
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            String text = extractTextContent(rawResponse);
            return responseParser.parseArray(text);
        } catch (Exception ex) {
            log.error("Claude normalization analysis failed: {}", ex.getMessage(), ex);
            throw new AiGenerationException("Claude provider failed to generate normalization analysis: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<Map<String, Object>> generateAnalysis(List<Map<String, Object>> tables, List<Map<String, Object>> relationships) {
        String prompt = promptBuilder.buildAnalysisPrompt(tables, relationships);

        Map<String, Object> body = Map.of(
                "model", model,
                "max_tokens", 2000,
                "messages", List.of(Map.of("role", "user", "content", prompt))
        );

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> rawResponse = restClient.post()
                    .uri("/v1/messages")
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            String text = extractTextContent(rawResponse);
            return responseParser.parseArray(text);
        } catch (Exception ex) {
            log.error("Claude analysis generation failed: {}", ex.getMessage(), ex);
            throw new AiGenerationException("Claude provider failed to generate analysis: " + ex.getMessage(), ex);
        }
    }

    @SuppressWarnings("unchecked")
    private String extractTextContent(Map<String, Object> rawResponse) {
        List<Map<String, Object>> content = (List<Map<String, Object>>) rawResponse.get("content");
        if (content == null || content.isEmpty()) {
            throw new IllegalStateException("Claude response contained no content blocks");
        }
        StringBuilder builder = new StringBuilder();
        for (Map<String, Object> block : content) {
            if ("text".equals(block.get("type"))) {
                builder.append(block.get("text"));
            }
        }
        return builder.toString();
    }
}