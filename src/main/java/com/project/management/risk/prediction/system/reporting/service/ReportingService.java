package com.project.management.risk.prediction.system.reporting.service;

import com.project.management.risk.prediction.system.common.api.RiskLevel;
import com.project.management.risk.prediction.system.common.exception.ResourceNotFoundException;
import com.project.management.risk.prediction.system.reporting.dto.RiskDistribution;
import com.project.management.risk.prediction.system.reporting.dto.SprintComparisonRow;
import com.project.management.risk.prediction.system.reporting.dto.TrendPoint;
import com.project.management.risk.prediction.system.risk.entity.AggregatedRiskResult;
import com.project.management.risk.prediction.system.risk.repository.AggregatedRiskResultRepository;
import com.project.management.risk.prediction.system.sprint.entity.Sprint;
import com.project.management.risk.prediction.system.sprint.repository.SprintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportingService {

    private final AggregatedRiskResultRepository aggregatedRepository;
    private final SprintRepository sprintRepository;

    public List<TrendPoint> sprintTrend(UUID sprintId) {
        if (!sprintRepository.existsById(sprintId)) {
            throw new ResourceNotFoundException("Sprint", sprintId);
        }
        return aggregatedRepository.findBySprintIdOrderByCreatedAtDesc(sprintId).stream()
                .map(this::toTrendPoint)
                .toList();
    }

    public List<SprintComparisonRow> compare(List<UUID> sprintIds) {
        Map<UUID, Sprint> sprintMap = new HashMap<>();
        sprintRepository.findAllById(sprintIds).forEach(s -> sprintMap.put(s.getId(), s));

        List<SprintComparisonRow> rows = new ArrayList<>(sprintIds.size());
        for (UUID id : sprintIds) {
            Sprint sprint = sprintMap.get(id);
            if (sprint == null) continue;
            AggregatedRiskResult latest = aggregatedRepository
                    .findFirstBySprintIdOrderByCreatedAtDesc(id).orElse(null);
            if (latest == null) {
                rows.add(new SprintComparisonRow(id, sprint.getName(), null, null, null, null));
            } else {
                rows.add(new SprintComparisonRow(
                        id, sprint.getName(),
                        latest.getOverBudgetScore(), latest.getReqChangeScore(),
                        latest.getOverallScore(), latest.getOverallLevel()));
            }
        }
        return rows;
    }

    public RiskDistribution distribution(String projectKey) {
        var page = sprintRepository.findByProjectKey(projectKey, PageRequest.of(0, Integer.MAX_VALUE));
        long low = 0, medium = 0, high = 0, unknown = 0, total = 0;
        for (Sprint s : page.getContent()) {
            AggregatedRiskResult latest = aggregatedRepository
                    .findFirstBySprintIdOrderByCreatedAtDesc(s.getId()).orElse(null);
            if (latest == null) continue;
            total++;
            switch (latest.getOverallLevel()) {
                case LOW -> low++;
                case MEDIUM -> medium++;
                case HIGH -> high++;
                case UNKNOWN -> unknown++;
            }
        }
        return new RiskDistribution(low, medium, high, unknown, total);
    }

    private TrendPoint toTrendPoint(AggregatedRiskResult r) {
        RiskLevel level = r.getOverallLevel();
        return new TrendPoint(r.getEvaluationRunId(), r.getCreatedAt(),
                r.getOverBudgetScore(), r.getReqChangeScore(),
                r.getOverallScore(), level, r.getDegraded());
    }
}
