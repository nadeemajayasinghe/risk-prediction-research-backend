package com.project.management.risk.prediction.system.reporting.controller;

import com.project.management.risk.prediction.system.common.api.ApiResponse;
import com.project.management.risk.prediction.system.reporting.dto.RiskDistribution;
import com.project.management.risk.prediction.system.reporting.dto.SprintComparisonRow;
import com.project.management.risk.prediction.system.reporting.dto.TrendPoint;
import com.project.management.risk.prediction.system.reporting.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportingController {

    private final ReportingService reportingService;

    @GetMapping("/sprints/{sprintId}/trend")
    public ApiResponse<List<TrendPoint>> trend(@PathVariable UUID sprintId) {
        return ApiResponse.ok(reportingService.sprintTrend(sprintId));
    }

    @GetMapping("/projects/{projectKey}/sprint-comparison")
    public ApiResponse<List<SprintComparisonRow>> compare(@PathVariable String projectKey,
                                                          @RequestParam List<UUID> sprintIds) {
        return ApiResponse.ok(reportingService.compare(sprintIds));
    }

    @GetMapping("/projects/{projectKey}/risk-distribution")
    public ApiResponse<RiskDistribution> distribution(@PathVariable String projectKey) {
        return ApiResponse.ok(reportingService.distribution(projectKey));
    }
}
