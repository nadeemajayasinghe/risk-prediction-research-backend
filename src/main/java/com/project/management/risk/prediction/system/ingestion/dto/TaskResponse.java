package com.project.management.risk.prediction.system.ingestion.dto;

import com.project.management.risk.prediction.system.ingestion.entity.WorkItemStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        UUID storyId,
        String title,
        String description,
        BigDecimal estimatedHours,
        BigDecimal actualHours,
        WorkItemStatus status,
        String assignee
) {}
