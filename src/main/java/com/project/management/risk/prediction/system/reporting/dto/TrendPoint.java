package com.project.management.risk.prediction.system.reporting.dto;

import com.project.management.risk.prediction.system.common.api.RiskLevel;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TrendPoint(
        UUID evaluationRunId,
        Instant evaluatedAt,
        BigDecimal overBudgetScore,
        BigDecimal reqChangeScore,
        BigDecimal overallScore,
        RiskLevel overallLevel,
        Boolean degraded
) {}
