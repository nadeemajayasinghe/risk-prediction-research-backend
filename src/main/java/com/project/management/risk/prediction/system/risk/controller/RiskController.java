package com.project.management.risk.prediction.system.risk.controller;

import com.project.management.risk.prediction.system.common.api.ApiResponse;
import com.project.management.risk.prediction.system.common.api.ModelType;
import com.project.management.risk.prediction.system.orchestrator.service.SprintRiskOrchestrationService;
import com.project.management.risk.prediction.system.risk.dto.RiskPredictionResponse;
import com.project.management.risk.prediction.system.risk.dto.RiskSummaryResponse;
import com.project.management.risk.prediction.system.risk.service.RiskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sprints/{sprintId}")
@RequiredArgsConstructor
public class RiskController {

    private final SprintRiskOrchestrationService orchestrationService;
    private final RiskService riskService;

    @PostMapping("/evaluate-risk")
    public ApiResponse<RiskSummaryResponse> evaluate(@PathVariable UUID sprintId,
                                                     @RequestParam(defaultValue = "false") boolean force) {
        return ApiResponse.ok(orchestrationService.evaluate(sprintId, force));
    }

    @GetMapping("/risk-summary")
    public ApiResponse<RiskSummaryResponse> summary(@PathVariable UUID sprintId) {
        return ApiResponse.ok(riskService.getLatestSummary(sprintId));
    }

    @GetMapping("/history")
    public ApiResponse<List<RiskSummaryResponse>> history(@PathVariable UUID sprintId) {
        return ApiResponse.ok(riskService.getHistory(sprintId));
    }

    @GetMapping("/risk-predictions")
    public ApiResponse<Page<RiskPredictionResponse>> predictions(
            @PathVariable UUID sprintId,
            @RequestParam(required = false) ModelType modelType,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok(riskService.getPredictions(sprintId, modelType, pageable));
    }
}
