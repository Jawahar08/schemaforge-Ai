-- ============================================================
-- V14__create_activities_table.sql
-- ============================================================

CREATE TABLE activities (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    actor_user_id   UUID,
    project_id      UUID,
    team_id         UUID,
    schema_id       UUID,
    activity_type   VARCHAR(60)  NOT NULL,
    entity_type     VARCHAR(40)  NOT NULL,
    entity_id       UUID,
    title           VARCHAR(500) NOT NULL,
    description     TEXT,
    metadata        JSONB        NOT NULL DEFAULT '{}'::jsonb,
    ip_address      VARCHAR(45),
    user_agent      VARCHAR(512),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT fk_activities_actor
        FOREIGN KEY (actor_user_id) REFERENCES users (id) ON DELETE SET NULL
);

CREATE INDEX idx_activities_actor_created
    ON activities (actor_user_id, created_at DESC)
    WHERE actor_user_id IS NOT NULL;

CREATE INDEX idx_activities_project_created
    ON activities (project_id, created_at DESC)
    WHERE project_id IS NOT NULL;

CREATE INDEX idx_activities_team_created
    ON activities (team_id, created_at DESC)
    WHERE team_id IS NOT NULL;

CREATE INDEX idx_activities_schema_created
    ON activities (schema_id, created_at DESC)
    WHERE schema_id IS NOT NULL;

CREATE INDEX idx_activities_type
    ON activities (activity_type);

CREATE INDEX idx_activities_created_at
    ON activities (created_at DESC);

COMMENT ON TABLE activities IS
    'User-facing activity feed — richer than audit_logs, supports project/team/schema scoping';