ALTER TABLE ai_requests
DROP CONSTRAINT chk_ai_requests_provider;

ALTER TABLE ai_requests
ADD CONSTRAINT chk_ai_requests_provider
CHECK (
    provider IN ('CLAUDE','OPENAI','GEMINI','MOCK')
);