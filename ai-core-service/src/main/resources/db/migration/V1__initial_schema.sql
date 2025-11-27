-- AI Logs Table
CREATE TABLE ai_logs (
    id UUID PRIMARY KEY,
    task_type VARCHAR(50) NOT NULL,
    request_timestamp TIMESTAMP NOT NULL,
    response_timestamp TIMESTAMP,
    token_count INTEGER,
    success BOOLEAN NOT NULL DEFAULT false,
    error_message TEXT,
    provider VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ai_logs_task_type ON ai_logs(task_type);
CREATE INDEX idx_ai_logs_timestamp ON ai_logs(request_timestamp);
CREATE INDEX idx_ai_logs_success ON ai_logs(success);

-- AI Costs Table
CREATE TABLE ai_costs (
    id UUID PRIMARY KEY,
    date DATE NOT NULL,
    provider VARCHAR(50) NOT NULL,
    task_type VARCHAR(50) NOT NULL,
    total_tokens INTEGER NOT NULL DEFAULT 0,
    request_count INTEGER NOT NULL DEFAULT 0,
    estimated_cost DECIMAL(10, 4) NOT NULL DEFAULT 0.0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(date, provider, task_type)
);

CREATE INDEX idx_ai_costs_date ON ai_costs(date);
CREATE INDEX idx_ai_costs_provider ON ai_costs(provider);

-- Prompt Templates Table (optional - templates primarily in YAML files)
CREATE TABLE prompt_templates (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    version VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    parameters TEXT,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_prompt_templates_name ON prompt_templates(name);
CREATE INDEX idx_prompt_templates_active ON prompt_templates(active);
