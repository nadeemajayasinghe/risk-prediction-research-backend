package com.project.management.risk.prediction.system.orchestrator.service;

import com.project.management.risk.prediction.system.ingestion.entity.*;
import com.project.management.risk.prediction.system.sprint.entity.Sprint;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Snapshot of all data needed to invoke the AI models for one sprint.
 */
@Data
@Builder
public class SprintContext {
    private Sprint sprint;
    private SprintMetrics latestMetrics;
    private List<UserStory> stories;
    private List<Task> tasks;
    private List<RequirementUpdate> requirementUpdates;
    private List<Comment> requirementComments;
    private List<Comment> storyComments;
}
