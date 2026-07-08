package com.schemaforge.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.rate-limit")
public class RateLimitProperties {

    private int loginMaxRequests = 10;
    private int loginWindowSeconds = 60;

    private int registrationMaxRequests = 5;
    private int registrationWindowSeconds = 300;

    private int aiGenerationMaxRequests = 20;
    private int aiGenerationWindowSeconds = 3600;

    private int exportMaxRequests = 30;
    private int exportWindowSeconds = 3600;

    // Getters and setters
    public int getLoginMaxRequests() { return loginMaxRequests; }
    public void setLoginMaxRequests(int v) { this.loginMaxRequests = v; }

    public int getLoginWindowSeconds() { return loginWindowSeconds; }
    public void setLoginWindowSeconds(int v) { this.loginWindowSeconds = v; }

    public int getRegistrationMaxRequests() { return registrationMaxRequests; }
    public void setRegistrationMaxRequests(int v) { this.registrationMaxRequests = v; }

    public int getRegistrationWindowSeconds() { return registrationWindowSeconds; }
    public void setRegistrationWindowSeconds(int v) { this.registrationWindowSeconds = v; }

    public int getAiGenerationMaxRequests() { return aiGenerationMaxRequests; }
    public void setAiGenerationMaxRequests(int v) { this.aiGenerationMaxRequests = v; }

    public int getAiGenerationWindowSeconds() { return aiGenerationWindowSeconds; }
    public void setAiGenerationWindowSeconds(int v) { this.aiGenerationWindowSeconds = v; }

    public int getExportMaxRequests() { return exportMaxRequests; }
    public void setExportMaxRequests(int v) { this.exportMaxRequests = v; }

    public int getExportWindowSeconds() { return exportWindowSeconds; }
    public void setExportWindowSeconds(int v) { this.exportWindowSeconds = v; }
}