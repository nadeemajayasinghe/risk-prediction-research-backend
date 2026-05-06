package com.project.management.risk.prediction.system.ingestion.dto;

import com.project.management.risk.prediction.system.ingestion.entity.WorkItemStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record UserStoryRequest(
        @Size(max = 100) String externalKey,
        @NotBlank @Size(max = 300) String title,
        String description,
        @PositiveOrZero Integer storyPoints,
        WorkItemStatus status,
        @Size(max = 20) String priority,
        @Size(max = 100) String assignee
) {}
