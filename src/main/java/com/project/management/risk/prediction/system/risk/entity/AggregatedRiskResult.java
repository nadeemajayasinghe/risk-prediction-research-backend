package com.project.management.risk.prediction.system.risk.entity;

import com.project.management.risk.prediction.system.common.api.RiskLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "aggregated_risk_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AggregatedRiskResult {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "sprint_id", nullable = false)
    private UUID sprintId;

    @Column(name = "evaluation_run_id", nullable = false, unique = true)
    private UUID evaluationRunId;

    @Column(name = "over_budget_score", precision = 6, scale = 2)
    private BigDecimal overBudgetScore;

    @Column(name = "req_change_score", precision = 6, scale = 2)
    private BigDecimal reqChangeScore;

    @Column(name = "overall_score", nullable = false, precision = 6, scale = 2)
    private BigDecimal overallScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "overall_level", nullable = false, length = 15)
    private RiskLevel overallLevel;

    @Column(name = "combined_explanation", columnDefinition = "TEXT")
    private String combinedExplanation;

    @Column(name = "weights_snapshot", columnDefinition = "TEXT")
    private String weightsSnapshot;

    @Column(name = "degraded", nullable = false)
    private Boolean degraded;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (id == null) id = UUID.randomUUID();
        createdAt = Instant.now();
        if (degraded == null) degraded = Boolean.FALSE;
    }
}
