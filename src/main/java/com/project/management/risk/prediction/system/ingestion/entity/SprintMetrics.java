package com.project.management.risk.prediction.system.ingestion.entity;

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
@Table(name = "sprint_metrics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SprintMetrics {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "sprint_id", nullable = false)
    private UUID sprintId;

    @Column(name = "planned_story_points", nullable = false)
    private Integer plannedStoryPoints;

    @Column(name = "completed_story_points", nullable = false)
    private Integer completedStoryPoints;

    @Column(name = "planned_effort_hours", nullable = false, precision = 10, scale = 2)
    private BigDecimal plannedEffortHours;

    @Column(name = "actual_effort_hours", nullable = false, precision = 10, scale = 2)
    private BigDecimal actualEffortHours;

    @Column(name = "effort_deviation_pct", precision = 7, scale = 2)
    private BigDecimal effortDeviationPct;

    @Column(name = "task_count", nullable = false)
    private Integer taskCount;

    @Column(name = "completed_task_count", nullable = false)
    private Integer completedTaskCount;

    @Column(name = "blocker_count", nullable = false)
    private Integer blockerCount;

    @Column(name = "snapshot_at", nullable = false)
    private Instant snapshotAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        if (id == null) id = UUID.randomUUID();
        Instant now = Instant.now();
        if (snapshotAt == null) snapshotAt = now;
        createdAt = now;
        updatedAt = now;
        if (plannedStoryPoints == null) plannedStoryPoints = 0;
        if (completedStoryPoints == null) completedStoryPoints = 0;
        if (plannedEffortHours == null) plannedEffortHours = BigDecimal.ZERO;
        if (actualEffortHours == null) actualEffortHours = BigDecimal.ZERO;
        if (taskCount == null) taskCount = 0;
        if (completedTaskCount == null) completedTaskCount = 0;
        if (blockerCount == null) blockerCount = 0;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}
