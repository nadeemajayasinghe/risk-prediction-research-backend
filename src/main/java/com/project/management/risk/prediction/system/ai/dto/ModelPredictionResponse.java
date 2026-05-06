package com.project.management.risk.prediction.system.ai.dto;

import com.project.management.risk.prediction.system.common.api.ModelType;
import com.project.management.risk.prediction.system.common.api.RiskLevel;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Internal, model-neutral shape that both AI clients map their external
 * responses into. The orchestrator and aggregation engine only see this.
 */
@Data
@Builder
public class ModelPredictionResponse {
    private ModelType modelType;
    private BigDecimal riskScore;
    private RiskLevel riskLevel;
    private BigDecimal probability;
    private String explanation;
    private String modelVersion;
    private long latencyMs;
    private boolean degraded;
}
