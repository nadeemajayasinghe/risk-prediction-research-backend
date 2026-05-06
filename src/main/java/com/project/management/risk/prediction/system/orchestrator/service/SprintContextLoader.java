package com.project.management.risk.prediction.system.orchestrator.service;

import com.project.management.risk.prediction.system.common.exception.ResourceNotFoundException;
import com.project.management.risk.prediction.system.ingestion.entity.*;
import com.project.management.risk.prediction.system.ingestion.repository.*;
import com.project.management.risk.prediction.system.sprint.entity.Sprint;
import com.project.management.risk.prediction.system.sprint.repository.SprintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SprintContextLoader {

    private final SprintRepository sprintRepository;
    private final SprintMetricsRepository metricsRepository;
    private final UserStoryRepository storyRepository;
    private final TaskRepository taskRepository;
    private final RequirementUpdateRepository requirementUpdateRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public SprintContext load(UUID sprintId) {
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint", sprintId));

        SprintMetrics latestMetrics = metricsRepository
                .findFirstBySprintIdOrderBySnapshotAtDesc(sprintId)
                .orElse(null);

        List<UserStory> stories = storyRepository.findBySprintId(sprintId);
        List<UUID> storyIds = stories.stream().map(UserStory::getId).toList();
        List<Task> tasks = storyIds.isEmpty()
                ? Collections.emptyList()
                : taskRepository.findByStoryIdIn(storyIds);

        List<RequirementUpdate> updates = requirementUpdateRepository.findBySprintIdOrderByChangedAtDesc(sprintId);

        List<UUID> requirementIds = updates.stream().map(RequirementUpdate::getId).toList();
        List<Comment> reqComments = requirementIds.isEmpty()
                ? Collections.emptyList()
                : commentRepository.findByParentTypeAndParentIdIn(CommentParentType.REQUIREMENT, requirementIds);

        List<Comment> storyComments = storyIds.isEmpty()
                ? Collections.emptyList()
                : commentRepository.findByParentTypeAndParentIdIn(CommentParentType.STORY, storyIds);

        return SprintContext.builder()
                .sprint(sprint)
                .latestMetrics(latestMetrics)
                .stories(stories)
                .tasks(tasks)
                .requirementUpdates(updates)
                .requirementComments(reqComments)
                .storyComments(storyComments)
                .build();
    }
}
