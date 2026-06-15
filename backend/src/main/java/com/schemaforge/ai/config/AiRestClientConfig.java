package com.schemaforge.ai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class AiRestClientConfig {

    @Value("${ai.claude.base-url}")
    private String claudeBaseUrl;

    @Value("${ai.openai.base-url}")
    private String openAiBaseUrl;

    @Value("${ai.gemini.base-url}")
    private String geminiBaseUrl;

    @Bean
    public RestClient claudeRestClient() {
        return RestClient.builder()
                .baseUrl(claudeBaseUrl)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public RestClient openAiRestClient() {
        return RestClient.builder()
                .baseUrl(openAiBaseUrl)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public RestClient geminiRestClient() {
        return RestClient.builder()
                .baseUrl(geminiBaseUrl)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}