package com.project.management.risk.prediction.system.ingestion.controller;

import com.project.management.risk.prediction.system.common.api.ApiResponse;
import com.project.management.risk.prediction.system.ingestion.dto.*;
import com.project.management.risk.prediction.system.ingestion.service.IngestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class IngestionController {

    private final IngestionService ingestionService;

    @PostMapping("/sprints/{sprintId}/metrics")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SprintMetricsResponse> addMetrics(@PathVariable UUID sprintId,
                                                         @Valid @RequestBody SprintMetricsRequest req) {
        return ApiResponse.ok(ingestionService.addMetrics(sprintId, req));
    }

    @PostMapping("/sprints/{sprintId}/stories")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserStoryResponse> addStory(@PathVariable UUID sprintId,
                                                   @Valid @RequestBody UserStoryRequest req) {
        return ApiResponse.ok(ingestionService.addStory(sprintId, req));
    }

    @PostMapping("/stories/{storyId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TaskResponse> addTask(@PathVariable UUID storyId,
                                             @Valid @RequestBody TaskRequest req) {
        return ApiResponse.ok(ingestionService.addTask(storyId, req));
    }

    @PostMapping("/sprints/{sprintId}/requirement-updates")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<RequirementUpdateResponse> addRequirementUpdate(@PathVariable UUID sprintId,
                                                                       @Valid @RequestBody RequirementUpdateRequest req) {
        return ApiResponse.ok(ingestionService.addRequirementUpdate(sprintId, req));
    }

    @PostMapping("/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CommentResponse> addComment(@Valid @RequestBody CommentRequest req) {
        return ApiResponse.ok(ingestionService.addComment(req));
    }
}
