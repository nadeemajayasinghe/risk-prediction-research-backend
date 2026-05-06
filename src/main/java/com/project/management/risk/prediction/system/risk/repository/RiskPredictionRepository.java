package com.project.management.risk.prediction.system.risk.repository;

import com.project.management.risk.prediction.system.common.api.ModelType;
import com.project.management.risk.prediction.system.risk.entity.RiskPrediction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface RiskPredictionRepository extends JpaRepository<RiskPrediction, UUID> {
    List<RiskPrediction> findByEvaluationRunId(UUID evaluationRunId);

    Page<RiskPrediction> findBySprintIdOrderByPredictedAtDesc(UUID sprintId, Pageable pageable);

    Page<RiskPrediction> findBySprintIdAndModelTypeOrderByPredictedAtDesc(
            UUID sprintId, ModelType modelType, Pageable pageable);

    List<RiskPrediction> findBySprintIdAndPredictedAtBetweenOrderByPredictedAtDesc(
            UUID sprintId, Instant from, Instant to);
}
