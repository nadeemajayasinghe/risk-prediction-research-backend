package com.project.management.risk.prediction.system.risk.dto;

import com.project.management.risk.prediction.system.common.api.ModelType;
import com.project.management.risk.prediction.system.common.api.RiskLevel;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record RiskPredictionResponse(
        UUID id,
        UUID sprintId,
        UUID evaluationRunId,
        ModelType modelType,
        BigDecimal riskScore,
        RiskLevel riskLevel,
        BigDecimal probability,
        String explanation,
        String modelVersion,
        Boolean degraded,
        Instant predictedAt
) {}
