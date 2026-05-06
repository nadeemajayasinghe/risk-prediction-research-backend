package com.project.management.risk.prediction.system.orchestrator.aggregation;

import com.project.management.risk.prediction.system.common.api.RiskLevel;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AggregatedRisk {
    private BigDecimal overBudgetScore;
    private BigDecimal reqChangeScore;
    private BigDecimal overallScore;
    private RiskLevel overallLevel;
    private String combinedExplanation;
    private String weightsSnapshot;   // JSON
    private boolean degraded;
}
