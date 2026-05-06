package com.project.management.risk.prediction.system.reporting.dto;

import com.project.management.risk.prediction.system.common.api.RiskLevel;

import java.math.BigDecimal;
import java.util.UUID;

public record SprintComparisonRow(
        UUID sprintId,
        String sprintName,
        BigDecimal overBudgetScore,
        BigDecimal reqChangeScore,
        BigDecimal overallScore,
        RiskLevel overallLevel
) {}
