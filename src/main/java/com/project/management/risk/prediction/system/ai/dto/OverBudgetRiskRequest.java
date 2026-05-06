package com.project.management.risk.prediction.system.ai.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Outbound payload to the Over-Budget Risk model service.
 * Built from sprint metrics + task aggregates.
 */
@Data
@Builder
public class OverBudgetRiskRequest {
    private UUID sprintId;
    private Integer plannedStoryPoints;
    private Integer completedStoryPoints;
    private BigDecimal plannedEffortHours;
    private BigDecimal actualEffortHours;
    private BigDecimal effortDeviationPct;
    private Integer taskCount;
    private Integer completedTaskCount;
    private Integer blockerCount;
    private Integer daysElapsed;
    private Integer daysRemaining;
}
