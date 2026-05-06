package com.project.management.risk.prediction.system.sprint.dto;

import com.project.management.risk.prediction.system.sprint.entity.SprintStatus;
import jakarta.validation.constraints.NotNull;

public record SprintStatusUpdateRequest(@NotNull SprintStatus status) {}
