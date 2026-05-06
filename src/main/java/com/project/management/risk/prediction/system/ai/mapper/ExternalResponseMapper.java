package com.project.management.risk.prediction.system.ai.mapper;

import com.project.management.risk.prediction.system.ai.dto.ExternalModelResponse;
import com.project.management.risk.prediction.system.ai.dto.ModelPredictionResponse;
import com.project.management.risk.prediction.system.common.api.ModelType;
import com.project.management.risk.prediction.system.common.api.RiskLevel;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ExternalResponseMapper {

    public ModelPredictionResponse toInternal(ModelType type,
                                              ExternalModelResponse external,
                                              long latencyMs) {
        BigDecimal score = clampScore(external.getRiskScore());
        RiskLevel level = parseLevel(external.getRiskLevel(), score);
        return ModelPredictionResponse.builder()
                .modelType(type)
                .riskScore(score)
                .riskLevel(level)
                .probability(external.getProbability())
                .explanation(external.getExplanation())
                .modelVersion(external.getModelVersion())
                .latencyMs(latencyMs)
                .degraded(false)
                .build();
    }

    private BigDecimal clampScore(BigDecimal raw) {
        if (raw == null) return BigDecimal.ZERO;
        BigDecimal v = raw.setScale(2, RoundingMode.HALF_UP);
        if (v.compareTo(BigDecimal.ZERO) < 0) return BigDecimal.ZERO;
        if (v.compareTo(BigDecimal.valueOf(100)) > 0) return BigDecimal.valueOf(100);
        return v;
    }

    private RiskLevel parseLevel(String rawLevel, BigDecimal score) {
        if (rawLevel != null) {
            try {
                return RiskLevel.valueOf(rawLevel.trim().toUpperCase());
            } catch (IllegalArgumentException ignored) { /* fall through */ }
        }
        // Derive from score if external didn't provide a level
        if (score == null) return RiskLevel.UNKNOWN;
        double s = score.doubleValue();
        if (s >= 67) return RiskLevel.HIGH;
        if (s >= 34) return RiskLevel.MEDIUM;
        return RiskLevel.LOW;
    }
}
