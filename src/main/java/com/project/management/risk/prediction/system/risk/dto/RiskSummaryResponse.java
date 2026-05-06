package com.project.management.risk.prediction.system.risk.dto;

import com.project.management.risk.prediction.system.common.api.RiskLevel;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record RiskSummaryResponse(
        UUID sprintId,
        UUID evaluationRunId,
        BigDecimal overBudgetScore,
        BigDecimal reqChangeScore,
        BigDecimal overallScore,
        RiskLevel overallLevel,
        String combinedExplanation,
        Boolean degraded,
        Instant evaluatedAt
) {}
