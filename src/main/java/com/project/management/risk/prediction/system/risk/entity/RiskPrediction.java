package com.project.management.risk.prediction.system.risk.entity;

import com.project.management.risk.prediction.system.common.api.ModelType;
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
@Table(name = "risk_predictions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskPrediction {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "sprint_id", nullable = false)
    private UUID sprintId;

    @Column(name = "evaluation_run_id", nullable = false)
    private UUID evaluationRunId;

    @Enumerated(EnumType.STRING)
    @Column(name = "model_type", nullable = false, length = 30)
    private ModelType modelType;

    @Column(name = "risk_score", nullable = false, precision = 6, scale = 2)
    private BigDecimal riskScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, length = 15)
    private RiskLevel riskLevel;

    @Column(name = "probability", precision = 5, scale = 4)
    private BigDecimal probability;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "model_version", length = 50)
    private String modelVersion;

    @Column(name = "degraded", nullable = false)
    private Boolean degraded;

    @Column(name = "predicted_at", nullable = false)
    private Instant predictedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (id == null) id = UUID.randomUUID();
        Instant now = Instant.now();
        if (predictedAt == null) predictedAt = now;
        createdAt = now;
        if (degraded == null) degraded = Boolean.FALSE;
    }
}
