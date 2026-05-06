package com.project.management.risk.prediction.system.sprint.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record SprintCreateRequest(
        @NotBlank @Size(max = 200) String name,
        @NotBlank @Size(max = 100) String projectKey,
        @Size(max = 5000) String goal,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate
) {}
