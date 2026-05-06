-- =====================================================================
-- V1: Initial schema for AI-LLM Sprint Risk Early Warning System
-- =====================================================================

CREATE TABLE sprints (
    id              UUID PRIMARY KEY,
    name            VARCHAR(200) NOT NULL,
    project_key     VARCHAR(100) NOT NULL,
    goal            TEXT,
    start_date      DATE NOT NULL,
    end_date        DATE NOT NULL,
    status          VARCHAR(20) NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),
    version         BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_sprint_status CHECK (status IN ('PLANNED','ACTIVE','COMPLETED','CANCELLED')),
    CONSTRAINT chk_sprint_dates  CHECK (end_date >= start_date)
);
CREATE INDEX idx_sprints_project_status ON sprints(project_key, status);

CREATE TABLE sprint_metrics (
    id                        UUID PRIMARY KEY,
    sprint_id                 UUID NOT NULL REFERENCES sprints(id) ON DELETE CASCADE,
    planned_story_points      INTEGER NOT NULL DEFAULT 0,
    completed_story_points    INTEGER NOT NULL DEFAULT 0,
    planned_effort_hours      NUMERIC(10,2) NOT NULL DEFAULT 0,
    actual_effort_hours       NUMERIC(10,2) NOT NULL DEFAULT 0,
    effort_deviation_pct      NUMERIC(7,2),
    task_count                INTEGER NOT NULL DEFAULT 0,
    completed_task_count      INTEGER NOT NULL DEFAULT 0,
    blocker_count             INTEGER NOT NULL DEFAULT 0,
    snapshot_at               TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at                TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_sprint_metrics_sprint ON sprint_metrics(sprint_id, snapshot_at DESC);

CREATE TABLE user_stories (
    id              UUID PRIMARY KEY,
    sprint_id       UUID NOT NULL REFERENCES sprints(id) ON DELETE CASCADE,
    external_key    VARCHAR(100),
    title           VARCHAR(300) NOT NULL,
    description     TEXT,
    story_points    INTEGER,
    status          VARCHAR(30) NOT NULL,
    priority        VARCHAR(20),
    assignee        VARCHAR(100),
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version         BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_story_status CHECK (status IN ('TODO','IN_PROGRESS','BLOCKED','REVIEW','DONE','CANCELLED'))
);
CREATE INDEX idx_user_stories_sprint ON user_stories(sprint_id);
CREATE INDEX idx_user_stories_external ON user_stories(external_key);

CREATE TABLE tasks (
    id              UUID PRIMARY KEY,
    story_id        UUID NOT NULL REFERENCES user_stories(id) ON DELETE CASCADE,
    title           VARCHAR(300) NOT NULL,
    description     TEXT,
    estimated_hours NUMERIC(8,2),
    actual_hours    NUMERIC(8,2),
    status          VARCHAR(30) NOT NULL,
    assignee        VARCHAR(100),
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version         BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_task_status CHECK (status IN ('TODO','IN_PROGRESS','BLOCKED','REVIEW','DONE','CANCELLED'))
);
CREATE INDEX idx_tasks_story ON tasks(story_id);

CREATE TABLE requirement_updates (
    id              UUID PRIMARY KEY,
    sprint_id       UUID NOT NULL REFERENCES sprints(id) ON DELETE CASCADE,
    story_id        UUID REFERENCES user_stories(id) ON DELETE SET NULL,
    revision_no     INTEGER NOT NULL,
    change_type     VARCHAR(20) NOT NULL,
    previous_text   TEXT,
    new_text        TEXT,
    rationale       TEXT,
    changed_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    changed_by      VARCHAR(100),
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_req_change_type CHECK (change_type IN ('ADD','MODIFY','REMOVE'))
);
CREATE INDEX idx_req_updates_sprint ON requirement_updates(sprint_id, changed_at DESC);
CREATE INDEX idx_req_updates_story ON requirement_updates(story_id);

CREATE TABLE comments (
    id              UUID PRIMARY KEY,
    parent_type     VARCHAR(20) NOT NULL,
    parent_id       UUID NOT NULL,
    author          VARCHAR(100),
    text            TEXT NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_comment_parent CHECK (parent_type IN ('STORY','REQUIREMENT'))
);
CREATE INDEX idx_comments_parent ON comments(parent_type, parent_id, created_at DESC);

-- =====================================================================
-- Risk & AI tables
-- =====================================================================

CREATE TABLE risk_predictions (
    id                    UUID PRIMARY KEY,
    sprint_id             UUID NOT NULL REFERENCES sprints(id) ON DELETE CASCADE,
    evaluation_run_id     UUID NOT NULL,
    model_type            VARCHAR(30) NOT NULL,
    risk_score            NUMERIC(6,2) NOT NULL,
    risk_level            VARCHAR(15) NOT NULL,
    probability           NUMERIC(5,4),
    explanation           TEXT,
    model_version         VARCHAR(50),
    degraded              BOOLEAN NOT NULL DEFAULT FALSE,
    predicted_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_pred_model_type CHECK (model_type IN ('OVER_BUDGET','REQ_CHANGE')),
    CONSTRAINT chk_pred_risk_level CHECK (risk_level IN ('LOW','MEDIUM','HIGH','UNKNOWN')),
    CONSTRAINT chk_pred_risk_score CHECK (risk_score >= 0 AND risk_score <= 100)
);
CREATE INDEX idx_risk_pred_sprint_time ON risk_predictions(sprint_id, predicted_at DESC);
CREATE INDEX idx_risk_pred_run ON risk_predictions(evaluation_run_id);

CREATE TABLE ai_model_responses (
    id                    UUID PRIMARY KEY,
    risk_prediction_id    UUID REFERENCES risk_predictions(id) ON DELETE SET NULL,
    sprint_id             UUID NOT NULL,
    model_type            VARCHAR(30) NOT NULL,
    request_payload       TEXT,
    response_payload      TEXT,
    http_status           INTEGER,
    latency_ms            BIGINT,
    error_message         TEXT,
    attempt_count         INTEGER NOT NULL DEFAULT 1,
    created_at            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_ai_resp_pred ON ai_model_responses(risk_prediction_id);
CREATE INDEX idx_ai_resp_sprint ON ai_model_responses(sprint_id, created_at DESC);

CREATE TABLE aggregated_risk_results (
    id                    UUID PRIMARY KEY,
    sprint_id             UUID NOT NULL REFERENCES sprints(id) ON DELETE CASCADE,
    evaluation_run_id     UUID NOT NULL UNIQUE,
    over_budget_score     NUMERIC(6,2),
    req_change_score      NUMERIC(6,2),
    overall_score         NUMERIC(6,2) NOT NULL,
    overall_level         VARCHAR(15) NOT NULL,
    combined_explanation  TEXT,
    weights_snapshot      TEXT,
    degraded              BOOLEAN NOT NULL DEFAULT FALSE,
    created_at            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_agg_overall_level CHECK (overall_level IN ('LOW','MEDIUM','HIGH','UNKNOWN')),
    CONSTRAINT chk_agg_overall_score CHECK (overall_score >= 0 AND overall_score <= 100)
);
CREATE INDEX idx_agg_sprint_time ON aggregated_risk_results(sprint_id, created_at DESC);
