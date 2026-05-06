package com.project.management.risk.prediction.system.event;

import com.project.management.risk.prediction.system.common.api.RiskLevel;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Published after a sprint risk evaluation completes. Replaceable
 * with a Kafka event when scaling out.
 */
@Data
@Builder
public class SprintRiskEvaluatedEvent {
    private UUID sprintId;
    private UUID evaluationRunId;
    private BigDecimal overallScore;
    private RiskLevel overallLevel;
    private boolean degraded;
    private Instant occurredAt;
}
