package com.project.management.risk.prediction.system.ingestion.dto;

import com.project.management.risk.prediction.system.ingestion.entity.CommentParentType;

import java.time.Instant;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        CommentParentType parentType,
        UUID parentId,
        String author,
        String text,
        Instant createdAt
) {}
