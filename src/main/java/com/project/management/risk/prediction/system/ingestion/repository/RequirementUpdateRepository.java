package com.project.management.risk.prediction.system.ingestion.repository;

import com.project.management.risk.prediction.system.ingestion.entity.RequirementUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RequirementUpdateRepository extends JpaRepository<RequirementUpdate, UUID> {
    List<RequirementUpdate> findBySprintIdOrderByChangedAtDesc(UUID sprintId);
    long countBySprintId(UUID sprintId);
}
