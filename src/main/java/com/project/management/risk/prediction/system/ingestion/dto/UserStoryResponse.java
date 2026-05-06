package com.project.management.risk.prediction.system.ingestion.dto;

import com.project.management.risk.prediction.system.ingestion.entity.WorkItemStatus;

import java.util.UUID;

public record UserStoryResponse(
        UUID id,
        UUID sprintId,
        String externalKey,
        String title,
        String description,
        Integer storyPoints,
        WorkItemStatus status,
        String priority,
        String assignee
) {}
