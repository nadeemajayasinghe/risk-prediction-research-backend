package com.project.management.risk.prediction.system.orchestrator.aggregation;

import com.project.management.risk.prediction.system.ai.dto.ModelPredictionResponse;

public interface AggregationStrategy {
    AggregatedRisk aggregate(ModelPredictionResponse overBudget,
                             ModelPredictionResponse reqChange);
}
