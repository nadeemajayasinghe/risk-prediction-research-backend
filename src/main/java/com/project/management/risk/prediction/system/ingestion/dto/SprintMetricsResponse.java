package com.project.management.risk.prediction.system.ingestion.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record SprintMetricsResponse(
        UUID id,
        UUID sprintId,
        Integer plannedStoryPoints,
        Integer completedStoryPoints,
        BigDecimal plannedEffortHours,
        BigDecimal actualEffortHours,
        BigDecimal effortDeviationPct,
        Integer taskCount,
        Integer completedTaskCount,
        Integer blockerCount,
        Instant snapshotAt
) {}
