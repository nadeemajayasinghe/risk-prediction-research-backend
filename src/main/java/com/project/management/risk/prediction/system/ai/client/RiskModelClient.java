package com.project.management.risk.prediction.system.ai.client;

import com.project.management.risk.prediction.system.ai.dto.ModelPredictionResponse;

/**
 * Internal contract for any risk-prediction model client.
 * Implementations handle transport, retries, and fallback;
 * all callers see only the neutral ModelPredictionResponse.
 */
public interface RiskModelClient<REQ> {
    ModelPredictionResponse predict(REQ request);
}
