package com.project.management.risk.prediction.system.ingestion.dto;

import com.project.management.risk.prediction.system.ingestion.entity.CommentParentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CommentRequest(
        @NotNull CommentParentType parentType,
        @NotNull UUID parentId,
        String author,
        @NotBlank String text
) {}
