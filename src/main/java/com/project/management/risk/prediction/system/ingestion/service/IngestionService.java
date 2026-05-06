package com.project.management.risk.prediction.system.ingestion.service;

import com.project.management.risk.prediction.system.common.exception.ResourceNotFoundException;
import com.project.management.risk.prediction.system.ingestion.dto.*;
import com.project.management.risk.prediction.system.ingestion.entity.*;
import com.project.management.risk.prediction.system.ingestion.repository.*;
import com.project.management.risk.prediction.system.sprint.repository.SprintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class IngestionService {

    private final SprintRepository sprintRepository;
    private final SprintMetricsRepository metricsRepository;
    private final UserStoryRepository userStoryRepository;
    private final TaskRepository taskRepository;
    private final RequirementUpdateRepository requirementUpdateRepository;
    private final CommentRepository commentRepository;

    private void requireSprint(UUID sprintId) {
        if (!sprintRepository.existsById(sprintId)) {
            throw new ResourceNotFoundException("Sprint", sprintId);
        }
    }

    private void requireStory(UUID storyId) {
        if (!userStoryRepository.existsById(storyId)) {
            throw new ResourceNotFoundException("UserStory", storyId);
        }
    }

    public SprintMetricsResponse addMetrics(UUID sprintId, SprintMetricsRequest req) {
        requireSprint(sprintId);
        BigDecimal deviation = computeDeviation(req.plannedEffortHours(), req.actualEffortHours());
        SprintMetrics m = SprintMetrics.builder()
                .sprintId(sprintId)
                .plannedStoryPoints(req.plannedStoryPoints())
                .completedStoryPoints(req.completedStoryPoints())
                .plannedEffortHours(req.plannedEffortHours())
                .actualEffortHours(req.actualEffortHours())
                .effortDeviationPct(deviation)
                .taskCount(req.taskCount())
                .completedTaskCount(req.completedTaskCount())
                .blockerCount(req.blockerCount())
                .build();
        SprintMetrics saved = metricsRepository.save(m);
        return toMetricsResponse(saved);
    }

    private BigDecimal computeDeviation(BigDecimal planned, BigDecimal actual) {
        if (planned == null || planned.compareTo(BigDecimal.ZERO) == 0) return null;
        return actual.subtract(planned)
                .divide(planned, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public UserStoryResponse addStory(UUID sprintId, UserStoryRequest req) {
        requireSprint(sprintId);
        UserStory s = UserStory.builder()
                .sprintId(sprintId)
                .externalKey(req.externalKey())
                .title(req.title())
                .description(req.description())
                .storyPoints(req.storyPoints())
                .status(req.status() != null ? req.status() : WorkItemStatus.TODO)
                .priority(req.priority())
                .assignee(req.assignee())
                .build();
        UserStory saved = userStoryRepository.save(s);
        return toStoryResponse(saved);
    }

    public TaskResponse addTask(UUID storyId, TaskRequest req) {
        requireStory(storyId);
        Task t = Task.builder()
                .storyId(storyId)
                .title(req.title())
                .description(req.description())
                .estimatedHours(req.estimatedHours())
                .actualHours(req.actualHours())
                .status(req.status() != null ? req.status() : WorkItemStatus.TODO)
                .assignee(req.assignee())
                .build();
        Task saved = taskRepository.save(t);
        return toTaskResponse(saved);
    }

    public RequirementUpdateResponse addRequirementUpdate(UUID sprintId, RequirementUpdateRequest req) {
        requireSprint(sprintId);
        if (req.storyId() != null) requireStory(req.storyId());
        RequirementUpdate u = RequirementUpdate.builder()
                .sprintId(sprintId)
                .storyId(req.storyId())
                .revisionNo(req.revisionNo())
                .changeType(req.changeType())
                .previousText(req.previousText())
                .newText(req.newText())
                .rationale(req.rationale())
                .changedBy(req.changedBy())
                .build();
        RequirementUpdate saved = requirementUpdateRepository.save(u);
        return toRequirementUpdateResponse(saved);
    }

    public CommentResponse addComment(CommentRequest req) {
        Comment c = Comment.builder()
                .parentType(req.parentType())
                .parentId(req.parentId())
                .author(req.author())
                .text(req.text())
                .build();
        Comment saved = commentRepository.save(c);
        return toCommentResponse(saved);
    }

    private SprintMetricsResponse toMetricsResponse(SprintMetrics m) {
        return new SprintMetricsResponse(
                m.getId(), m.getSprintId(),
                m.getPlannedStoryPoints(), m.getCompletedStoryPoints(),
                m.getPlannedEffortHours(), m.getActualEffortHours(),
                m.getEffortDeviationPct(),
                m.getTaskCount(), m.getCompletedTaskCount(), m.getBlockerCount(),
                m.getSnapshotAt());
    }

    private UserStoryResponse toStoryResponse(UserStory s) {
        return new UserStoryResponse(
                s.getId(), s.getSprintId(), s.getExternalKey(),
                s.getTitle(), s.getDescription(), s.getStoryPoints(),
                s.getStatus(), s.getPriority(), s.getAssignee());
    }

    private TaskResponse toTaskResponse(Task t) {
        return new TaskResponse(
                t.getId(), t.getStoryId(), t.getTitle(), t.getDescription(),
                t.getEstimatedHours(), t.getActualHours(), t.getStatus(), t.getAssignee());
    }

    private RequirementUpdateResponse toRequirementUpdateResponse(RequirementUpdate u) {
        return new RequirementUpdateResponse(
                u.getId(), u.getSprintId(), u.getStoryId(), u.getRevisionNo(),
                u.getChangeType(), u.getPreviousText(), u.getNewText(), u.getRationale(),
                u.getChangedAt(), u.getChangedBy());
    }

    private CommentResponse toCommentResponse(Comment c) {
        return new CommentResponse(c.getId(), c.getParentType(), c.getParentId(),
                c.getAuthor(), c.getText(), c.getCreatedAt());
    }
}
