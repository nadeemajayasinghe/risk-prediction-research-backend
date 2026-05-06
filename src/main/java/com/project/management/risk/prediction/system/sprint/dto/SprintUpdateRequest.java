package com.project.management.risk.prediction.system.sprint.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record SprintUpdateRequest(
        @Size(max = 200) String name,
        @Size(max = 5000) String goal,
        LocalDate startDate,
        LocalDate endDate
) {}
