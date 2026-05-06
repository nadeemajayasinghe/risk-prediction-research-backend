package com.project.management.risk.prediction.system.sprint.dto;

import com.project.management.risk.prediction.system.sprint.entity.SprintStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record SprintResponse(
        UUID id,
        String name,
        String projectKey,
        String goal,
        LocalDate startDate,
        LocalDate endDate,
        SprintStatus status,
        Instant createdAt,
        Instant updatedAt
) {}
