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
public class OpenAiProviderClient implements AiProviderClient {

    private final RestClient restClient;
    private final PromptBuilder promptBuilder;
    private final AiResponseParser responseParser;
    private final String apiKey;
    private final String model;

    public OpenAiProviderClient(
            @Qualifier("openAiRestClient") RestClient restClient,
            PromptBuilder promptBuilder,
            AiResponseParser responseParser,
            @Value("${ai.openai.api-key}") String apiKey,
            @Value("${ai.openai.model}") String model
    ) {
        this.restClient = restClient;
        this.promptBuilder = promptBuilder;
        this.responseParser = responseParser;
        this.apiKey = apiKey;
        this.model = model;
    }

    @Override
    public AiProvider getProviderType() {
        return AiProvider.OPENAI;
    }

    @Override
    public AiSchemaGenerationResult generateSchema(String systemDescription, String normalizationTarget) {
        String prompt = promptBuilder.buildSchemaGenerationPrompt(systemDescription, normalizationTarget);
        long start = System.currentTimeMillis();

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "response_format", Map.of("type", "json_object")
        );

        try {
            Map<String, Object> rawResponse = callChatCompletions(body);
            long latency = System.currentTimeMillis() - start;

            String text = extractTextContent(rawResponse);
            Map<String, Object> parsed = responseParser.parseObject(text);

            @SuppressWarnings("unchecked")
            Map<String, Object> usage = (Map<String, Object>) rawResponse.getOrDefault("usage", Map.of());
            int promptTokens = ((Number) usage.getOrDefault("prompt_tokens", 0)).intValue();
            int completionTokens = ((Number) usage.getOrDefault("completion_tokens", 0)).intValue();

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
            log.error("OpenAI schema generation failed: {}", ex.getMessage(), ex);
            throw new AiGenerationException("OpenAI provider failed to generate schema: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<Map<String, Object>> generateNormalization(List<Map<String, Object>> tables, String normalizationTarget) {
        String prompt = promptBuilder.buildNormalizationPrompt(tables, normalizationTarget);

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(Map.of("role", "user", "content", prompt))
        );

        try {
            Map<String, Object> rawResponse = callChatCompletions(body);
            String text = extractTextContent(rawResponse);
            return responseParser.parseArray(text);
        } catch (Exception ex) {
            log.error("OpenAI normalization analysis failed: {}", ex.getMessage(), ex);
            throw new AiGenerationException("OpenAI provider failed to generate normalization analysis: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<Map<String, Object>> generateAnalysis(List<Map<String, Object>> tables, List<Map<String, Object>> relationships) {
        String prompt = promptBuilder.buildAnalysisPrompt(tables, relationships);

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(Map.of("role", "user", "content", prompt))
        );

        try {
            Map<String, Object> rawResponse = callChatCompletions(body);
            String text = extractTextContent(rawResponse);
            return responseParser.parseArray(text);
        } catch (Exception ex) {
            log.error("OpenAI analysis generation failed: {}", ex.getMessage(), ex);
            throw new AiGenerationException("OpenAI provider failed to generate analysis: " + ex.getMessage(), ex);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> callChatCompletions(Map<String, Object> body) {
        return restClient.post()
                .uri("/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .body(body)
                .retrieve()
                .body(Map.class);
    }

    @SuppressWarnings("unchecked")
    private String extractTextContent(Map<String, Object> rawResponse) {
        List<Map<String, Object>> choices = (List<Map<String, Object>>) rawResponse.get("choices");
        if (choices == null || choices.isEmpty()) {
            throw new IllegalStateException("OpenAI response contained no choices");
        }
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        return (String) message.get("content");
    }
}