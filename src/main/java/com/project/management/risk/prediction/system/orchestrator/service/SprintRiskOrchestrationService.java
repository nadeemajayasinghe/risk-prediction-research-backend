package com.project.management.risk.prediction.system.orchestrator.service;

import com.project.management.risk.prediction.system.ai.client.OverBudgetRiskClient;
import com.project.management.risk.prediction.system.ai.client.RequirementChangeRiskClient;
import com.project.management.risk.prediction.system.ai.dto.ModelPredictionResponse;
import com.project.management.risk.prediction.system.ai.dto.OverBudgetRiskRequest;
import com.project.management.risk.prediction.system.ai.dto.RequirementChangeRiskRequest;
import com.project.management.risk.prediction.system.common.api.ModelType;
import com.project.management.risk.prediction.system.event.SprintRiskEvaluatedEvent;
import com.project.management.risk.prediction.system.orchestrator.aggregation.AggregatedRisk;
import com.project.management.risk.prediction.system.orchestrator.aggregation.AggregationStrategy;
import com.project.management.risk.prediction.system.risk.dto.RiskSummaryResponse;
import com.project.management.risk.prediction.system.risk.entity.AggregatedRiskResult;
import com.project.management.risk.prediction.system.risk.entity.RiskPrediction;
import com.project.management.risk.prediction.system.risk.repository.AggregatedRiskResultRepository;
import com.project.management.risk.prediction.system.risk.repository.RiskPredictionRepository;
import com.project.management.risk.prediction.system.risk.service.RiskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Central engine: collects sprint context, fans out to both AI models in parallel,
 * persists each prediction (with audit), aggregates, persists the aggregate, and
 * publishes a domain event. Exposes a single result DTO for the controller.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SprintRiskOrchestrationService {

    private final SprintContextLoader contextLoader;
    private final AiRequestBuilder requestBuilder;
    private final OverBudgetRiskClient overBudgetClient;
    private final RequirementChangeRiskClient reqChangeClient;
    private final AggregationStrategy aggregationStrategy;
    private final RiskPredictionRepository predictionRepository;
    private final AggregatedRiskResultRepository aggregatedRepository;
    private final RiskService riskService;
    private final ApplicationEventPublisher eventPublisher;

    @Qualifier("aiCallExecutor")
    private final Executor aiCallExecutor;

    @Transactional
    public RiskSummaryResponse evaluate(UUID sprintId, boolean force) {
        UUID runId = UUID.randomUUID();
        log.info("Starting risk evaluation: sprintId={} evaluationRunId={} force={}",
                sprintId, runId, force);

        SprintContext ctx = contextLoader.load(sprintId);

        OverBudgetRiskRequest obReq = requestBuilder.buildOverBudget(ctx);
        RequirementChangeRiskRequest rcReq = requestBuilder.buildReqChange(ctx);

        CompletableFuture<ModelPredictionResponse> obFuture =
                CompletableFuture.supplyAsync(() -> overBudgetClient.predict(obReq), aiCallExecutor);
        CompletableFuture<ModelPredictionResponse> rcFuture =
                CompletableFuture.supplyAsync(() -> reqChangeClient.predict(rcReq), aiCallExecutor);

        CompletableFuture.allOf(obFuture, rcFuture).join();

        ModelPredictionResponse obResp = obFuture.join();
        ModelPredictionResponse rcResp = rcFuture.join();

        RiskPrediction obSaved = persistPrediction(sprintId, runId, obResp);
        RiskPrediction rcSaved = persistPrediction(sprintId, runId, rcResp);

        AggregatedRisk aggregated = aggregationStrategy.aggregate(obResp, rcResp);
        AggregatedRiskResult savedAgg = persistAggregate(sprintId, runId, aggregated);

        eventPublisher.publishEvent(SprintRiskEvaluatedEvent.builder()
                .sprintId(sprintId)
                .evaluationRunId(runId)
                .overallScore(savedAgg.getOverallScore())
                .overallLevel(savedAgg.getOverallLevel())
                .degraded(savedAgg.getDegraded())
                .occurredAt(Instant.now())
                .build());

        log.info("Completed risk evaluation: sprintId={} runId={} overallScore={} level={} degraded={}",
                sprintId, runId, savedAgg.getOverallScore(), savedAgg.getOverallLevel(), savedAgg.getDegraded());

        return riskService.toSummary(savedAgg);
    }

    private RiskPrediction persistPrediction(UUID sprintId, UUID runId, ModelPredictionResponse resp) {
        RiskPrediction p = RiskPrediction.builder()
                .sprintId(sprintId)
                .evaluationRunId(runId)
                .modelType(resp.getModelType() != null ? resp.getModelType() : ModelType.OVER_BUDGET)
                .riskScore(resp.getRiskScore())
                .riskLevel(resp.getRiskLevel())
                .probability(resp.getProbability())
                .explanation(resp.getExplanation())
                .modelVersion(resp.getModelVersion())
                .degraded(resp.isDegraded())
                .build();
        return predictionRepository.save(p);
    }

    private AggregatedRiskResult persistAggregate(UUID sprintId, UUID runId, AggregatedRisk aggregated) {
        AggregatedRiskResult r = AggregatedRiskResult.builder()
                .sprintId(sprintId)
                .evaluationRunId(runId)
                .overBudgetScore(aggregated.getOverBudgetScore())
                .reqChangeScore(aggregated.getReqChangeScore())
                .overallScore(aggregated.getOverallScore())
                .overallLevel(aggregated.getOverallLevel())
                .combinedExplanation(aggregated.getCombinedExplanation())
                .weightsSnapshot(aggregated.getWeightsSnapshot())
                .degraded(aggregated.isDegraded())
                .build();
        return aggregatedRepository.save(r);
    }
}
