package com.project.management.risk.prediction.system.orchestrator.aggregation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.management.risk.prediction.system.ai.dto.ModelPredictionResponse;
import com.project.management.risk.prediction.system.common.api.RiskLevel;
import com.project.management.risk.prediction.system.common.exception.AggregationException;
import com.project.management.risk.prediction.system.config.AggregationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Default weighted aggregation:
 *   overall = w_b * overBudget + w_r * reqChange
 *
 * Special rules:
 *  - If only one model is available (the other is degraded), weights renormalize.
 *  - If both are degraded, overall = 0 / UNKNOWN, degraded=true.
 *  - Escalation: any individual score >= singleScoreFloor forces overallLevel >= HIGH.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WeightedAggregationStrategy implements AggregationStrategy {

    private final AggregationProperties props;
    private final ObjectMapper objectMapper;

    @Override
    public AggregatedRisk aggregate(ModelPredictionResponse overBudget,
                                    ModelPredictionResponse reqChange) {
        if (overBudget == null || reqChange == null) {
            throw new AggregationException("Both prediction inputs are required (use degraded result instead of null)");
        }

        BigDecimal wB = props.getWeights().getOverBudget();
        BigDecimal wR = props.getWeights().getReqChange();

        boolean obDegraded = overBudget.isDegraded();
        boolean rcDegraded = reqChange.isDegraded();

        BigDecimal obScore = nz(overBudget.getRiskScore());
        BigDecimal rcScore = nz(reqChange.getRiskScore());

        BigDecimal overall;
        boolean degraded = obDegraded || rcDegraded;
        StringBuilder explanation = new StringBuilder();

        if (obDegraded && rcDegraded) {
            overall = BigDecimal.ZERO;
            explanation.append("Both AI models unavailable; risk unknown. ");
        } else if (obDegraded) {
            overall = rcScore;
            explanation.append("Over-budget model unavailable; falling back to requirement-change score only. ");
        } else if (rcDegraded) {
            overall = obScore;
            explanation.append("Requirement-change model unavailable; falling back to over-budget score only. ");
        } else {
            BigDecimal totalWeight = wB.add(wR);
            overall = obScore.multiply(wB).add(rcScore.multiply(wR))
                    .divide(totalWeight, 2, RoundingMode.HALF_UP);
        }
        overall = clamp(overall);

        // Escalation: any single score over the floor forces HIGH
        BigDecimal floor = props.getEscalation().getSingleScoreFloor();
        boolean escalated = (!obDegraded && obScore.compareTo(floor) >= 0)
                || (!rcDegraded && rcScore.compareTo(floor) >= 0);

        RiskLevel level = (obDegraded && rcDegraded) ? RiskLevel.UNKNOWN : levelFor(overall);
        if (escalated && level != RiskLevel.UNKNOWN) {
            level = RiskLevel.HIGH;
            explanation.append("Escalated to HIGH because a single model score reached or exceeded ")
                    .append(floor).append(". ");
        }

        if (overBudget.getExplanation() != null && !obDegraded) {
            explanation.append("Over-budget: ").append(overBudget.getExplanation()).append(" ");
        }
        if (reqChange.getExplanation() != null && !rcDegraded) {
            explanation.append("Req-change: ").append(reqChange.getExplanation()).append(" ");
        }

        return AggregatedRisk.builder()
                .overBudgetScore(obDegraded ? null : obScore)
                .reqChangeScore(rcDegraded ? null : rcScore)
                .overallScore(overall)
                .overallLevel(level)
                .combinedExplanation(explanation.toString().trim())
                .weightsSnapshot(buildWeightsSnapshot(wB, wR, obDegraded, rcDegraded))
                .degraded(degraded)
                .build();
    }

    private RiskLevel levelFor(BigDecimal score) {
        if (score.compareTo(props.getThresholds().getHigh()) >= 0) return RiskLevel.HIGH;
        if (score.compareTo(props.getThresholds().getMedium()) >= 0) return RiskLevel.MEDIUM;
        return RiskLevel.LOW;
    }

    private BigDecimal clamp(BigDecimal v) {
        if (v == null) return BigDecimal.ZERO;
        if (v.compareTo(BigDecimal.ZERO) < 0) return BigDecimal.ZERO;
        if (v.compareTo(BigDecimal.valueOf(100)) > 0) return BigDecimal.valueOf(100);
        return v;
    }

    private BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private String buildWeightsSnapshot(BigDecimal wB, BigDecimal wR,
                                        boolean obDegraded, boolean rcDegraded) {
        Map<String, Object> snap = new LinkedHashMap<>();
        snap.put("overBudgetWeight", wB);
        snap.put("reqChangeWeight", wR);
        snap.put("overBudgetDegraded", obDegraded);
        snap.put("reqChangeDegraded", rcDegraded);
        snap.put("mediumThreshold", props.getThresholds().getMedium());
        snap.put("highThreshold", props.getThresholds().getHigh());
        snap.put("singleScoreFloor", props.getEscalation().getSingleScoreFloor());
        try {
            return objectMapper.writeValueAsString(snap);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize weights snapshot: {}", e.getMessage());
            return snap.toString();
        }
    }
}
