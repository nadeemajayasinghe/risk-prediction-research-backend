package com.project.management.risk.prediction.system.ingestion.repository;

import com.project.management.risk.prediction.system.ingestion.entity.SprintMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SprintMetricsRepository extends JpaRepository<SprintMetrics, UUID> {
    List<SprintMetrics> findBySprintIdOrderBySnapshotAtDesc(UUID sprintId);
    Optional<SprintMetrics> findFirstBySprintIdOrderBySnapshotAtDesc(UUID sprintId);
}
