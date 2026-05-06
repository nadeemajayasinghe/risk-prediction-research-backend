package com.project.management.risk.prediction.system.ingestion.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record SprintMetricsRequest(
        @NotNull @PositiveOrZero Integer plannedStoryPoints,
        @NotNull @PositiveOrZero Integer completedStoryPoints,
        @NotNull @PositiveOrZero BigDecimal plannedEffortHours,
        @NotNull @PositiveOrZero BigDecimal actualEffortHours,
        @NotNull @PositiveOrZero Integer taskCount,
        @NotNull @PositiveOrZero Integer completedTaskCount,
        @NotNull @PositiveOrZero Integer blockerCount
) {}
