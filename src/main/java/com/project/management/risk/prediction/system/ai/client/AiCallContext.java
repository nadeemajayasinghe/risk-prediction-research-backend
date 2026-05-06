package com.project.management.risk.prediction.system.ai.client;

import com.project.management.risk.prediction.system.common.api.ModelType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * ThreadLocal-style call context carried into the AI client invocation
 * so the client can write a complete audit log row for each attempt.
 */
@Data
@Builder
public class AiCallContext {
    private UUID sprintId;
    private UUID evaluationRunId;
    private ModelType modelType;
}
