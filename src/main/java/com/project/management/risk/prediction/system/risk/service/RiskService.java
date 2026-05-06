package com.project.management.risk.prediction.system.risk.service;

import com.project.management.risk.prediction.system.common.api.ModelType;
import com.project.management.risk.prediction.system.common.exception.ResourceNotFoundException;
import com.project.management.risk.prediction.system.risk.dto.RiskPredictionResponse;
import com.project.management.risk.prediction.system.risk.dto.RiskSummaryResponse;
import com.project.management.risk.prediction.system.risk.entity.AggregatedRiskResult;
import com.project.management.risk.prediction.system.risk.entity.RiskPrediction;
import com.project.management.risk.prediction.system.risk.repository.AggregatedRiskResultRepository;
import com.project.management.risk.prediction.system.risk.repository.RiskPredictionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RiskService {

    private final RiskPredictionRepository riskPredictionRepository;
    private final AggregatedRiskResultRepository aggregatedRepository;

    public RiskSummaryResponse getLatestSummary(UUID sprintId) {
        AggregatedRiskResult latest = aggregatedRepository
                .findFirstBySprintIdOrderByCreatedAtDesc(sprintId)
                .orElseThrow(() -> new ResourceNotFoundException("AggregatedRiskResult for sprint", sprintId));
        return toSummary(latest);
    }

    public List<RiskSummaryResponse> getHistory(UUID sprintId) {
        return aggregatedRepository.findBySprintIdOrderByCreatedAtDesc(sprintId)
                .stream().map(this::toSummary).toList();
    }

    public Page<RiskPredictionResponse> getPredictions(UUID sprintId, ModelType modelType, Pageable pageable) {
        Page<RiskPrediction> page = (modelType == null)
                ? riskPredictionRepository.findBySprintIdOrderByPredictedAtDesc(sprintId, pageable)
                : riskPredictionRepository.findBySprintIdAndModelTypeOrderByPredictedAtDesc(sprintId, modelType, pageable);
        return page.map(this::toPredictionResponse);
    }

    public RiskSummaryResponse toSummary(AggregatedRiskResult r) {
        return new RiskSummaryResponse(
                r.getSprintId(), r.getEvaluationRunId(),
                r.getOverBudgetScore(), r.getReqChangeScore(),
                r.getOverallScore(), r.getOverallLevel(),
                r.getCombinedExplanation(), r.getDegraded(),
                r.getCreatedAt());
    }

    public RiskPredictionResponse toPredictionResponse(RiskPrediction p) {
        return new RiskPredictionResponse(
                p.getId(), p.getSprintId(), p.getEvaluationRunId(),
                p.getModelType(), p.getRiskScore(), p.getRiskLevel(),
                p.getProbability(), p.getExplanation(), p.getModelVersion(),
                p.getDegraded(), p.getPredictedAt());
    }
}
