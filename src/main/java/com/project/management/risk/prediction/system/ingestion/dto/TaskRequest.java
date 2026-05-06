package com.project.management.risk.prediction.system.ingestion.dto;

import com.project.management.risk.prediction.system.ingestion.entity.WorkItemStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record TaskRequest(
        @NotBlank @Size(max = 300) String title,
        String description,
        @PositiveOrZero BigDecimal estimatedHours,
        @PositiveOrZero BigDecimal actualHours,
        WorkItemStatus status,
        @Size(max = 100) String assignee
) {}
