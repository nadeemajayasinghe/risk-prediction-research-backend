package com.project.management.risk.prediction.system.sprint.controller;

import com.project.management.risk.prediction.system.common.api.ApiResponse;
import com.project.management.risk.prediction.system.sprint.dto.SprintCreateRequest;
import com.project.management.risk.prediction.system.sprint.dto.SprintResponse;
import com.project.management.risk.prediction.system.sprint.dto.SprintStatusUpdateRequest;
import com.project.management.risk.prediction.system.sprint.dto.SprintUpdateRequest;
import com.project.management.risk.prediction.system.sprint.entity.SprintStatus;
import com.project.management.risk.prediction.system.sprint.service.SprintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sprints")
@RequiredArgsConstructor
public class SprintController {

    private final SprintService sprintService;

    @PostMapping
    public ResponseEntity<ApiResponse<SprintResponse>> create(@Valid @RequestBody SprintCreateRequest req) {
        SprintResponse created = sprintService.create(req);
        return ResponseEntity
                .created(URI.create("/api/v1/sprints/" + created.id()))
                .body(ApiResponse.ok(created));
    }

    @GetMapping("/{id}")
    public ApiResponse<SprintResponse> getById(@PathVariable UUID id) {
        return ApiResponse.ok(sprintService.getById(id));
    }

    @GetMapping
    public ApiResponse<Page<SprintResponse>> list(
            @RequestParam(required = false) String projectKey,
            @RequestParam(required = false) SprintStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok(sprintService.list(projectKey, status, pageable));
    }

    @PutMapping("/{id}")
    public ApiResponse<SprintResponse> update(@PathVariable UUID id,
                                              @Valid @RequestBody SprintUpdateRequest req) {
        return ApiResponse.ok(sprintService.update(id, req));
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<SprintResponse> updateStatus(@PathVariable UUID id,
                                                    @Valid @RequestBody SprintStatusUpdateRequest req) {
        return ApiResponse.ok(sprintService.updateStatus(id, req));
    }
}
