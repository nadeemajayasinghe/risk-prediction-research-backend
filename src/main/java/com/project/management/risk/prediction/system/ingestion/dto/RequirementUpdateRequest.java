package com.project.management.risk.prediction.system.ingestion.dto;

import com.project.management.risk.prediction.system.ingestion.entity.RequirementChangeType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record RequirementUpdateRequest(
        UUID storyId,
        @NotNull @Positive Integer revisionNo,
        @NotNull RequirementChangeType changeType,
        String previousText,
        String newText,
        String rationale,
        String changedBy
) {}
