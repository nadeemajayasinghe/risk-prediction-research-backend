package com.project.management.risk.prediction.system.ingestion.dto;

import com.project.management.risk.prediction.system.ingestion.entity.RequirementChangeType;

import java.time.Instant;
import java.util.UUID;

public record RequirementUpdateResponse(
        UUID id,
        UUID sprintId,
        UUID storyId,
        Integer revisionNo,
        RequirementChangeType changeType,
        String previousText,
        String newText,
        String rationale,
        Instant changedAt,
        String changedBy
) {}
