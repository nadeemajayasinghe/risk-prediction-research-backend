package com.project.management.risk.prediction.system.risk.repository;

import com.project.management.risk.prediction.system.risk.entity.AiModelResponseLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AiModelResponseLogRepository extends JpaRepository<AiModelResponseLog, UUID> {
    List<AiModelResponseLog> findBySprintIdOrderByCreatedAtDesc(UUID sprintId);
    List<AiModelResponseLog> findByRiskPredictionId(UUID riskPredictionId);
}
