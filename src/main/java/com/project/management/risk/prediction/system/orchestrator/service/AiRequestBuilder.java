package com.project.management.risk.prediction.system.orchestrator.service;

import com.project.management.risk.prediction.system.ai.dto.OverBudgetRiskRequest;
import com.project.management.risk.prediction.system.ai.dto.RequirementChangeRiskRequest;
import com.project.management.risk.prediction.system.ingestion.entity.Comment;
import com.project.management.risk.prediction.system.ingestion.entity.RequirementUpdate;
import com.project.management.risk.prediction.system.ingestion.entity.SprintMetrics;
import com.project.management.risk.prediction.system.ingestion.entity.UserStory;
import com.project.management.risk.prediction.system.sprint.entity.Sprint;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class AiRequestBuilder {

    public OverBudgetRiskRequest buildOverBudget(SprintContext ctx) {
        Sprint sprint = ctx.getSprint();
        SprintMetrics m = ctx.getLatestMetrics();
        LocalDate today = LocalDate.now();
        int daysElapsed = (int) Math.max(0, ChronoUnit.DAYS.between(sprint.getStartDate(), today));
        int daysRemaining = (int) Math.max(0, ChronoUnit.DAYS.between(today, sprint.getEndDate()));

        var builder = OverBudgetRiskRequest.builder()
                .sprintId(sprint.getId())
                .daysElapsed(daysElapsed)
                .daysRemaining(daysRemaining);

        if (m != null) {
            builder.plannedStoryPoints(m.getPlannedStoryPoints())
                    .completedStoryPoints(m.getCompletedStoryPoints())
                    .plannedEffortHours(m.getPlannedEffortHours())
                    .actualEffortHours(m.getActualEffortHours())
                    .effortDeviationPct(m.getEffortDeviationPct())
                    .taskCount(m.getTaskCount())
                    .completedTaskCount(m.getCompletedTaskCount())
                    .blockerCount(m.getBlockerCount());
        } else {
            builder.plannedStoryPoints(0).completedStoryPoints(0)
                    .plannedEffortHours(BigDecimal.ZERO).actualEffortHours(BigDecimal.ZERO)
                    .taskCount(0).completedTaskCount(0).blockerCount(0);
        }
        return builder.build();
    }

    public RequirementChangeRiskRequest buildReqChange(SprintContext ctx) {
        List<RequirementChangeRiskRequest.StoryText> stories = ctx.getStories().stream()
                .map(this::toStoryText)
                .toList();

        List<RequirementChangeRiskRequest.RequirementChange> changes = ctx.getRequirementUpdates().stream()
                .map(this::toChange)
                .toList();

        List<RequirementChangeRiskRequest.CommentText> comments = java.util.stream.Stream.concat(
                        ctx.getStoryComments().stream().map(c -> toCommentText("STORY", c)),
                        ctx.getRequirementComments().stream().map(c -> toCommentText("REQUIREMENT", c)))
                .toList();

        return RequirementChangeRiskRequest.builder()
                .sprintId(ctx.getSprint().getId())
                .stories(stories)
                .requirementChanges(changes)
                .comments(comments)
                .build();
    }

    private RequirementChangeRiskRequest.StoryText toStoryText(UserStory s) {
        return RequirementChangeRiskRequest.StoryText.builder()
                .id(s.getId())
                .externalKey(s.getExternalKey())
                .title(s.getTitle())
                .description(s.getDescription())
                .status(s.getStatus() != null ? s.getStatus().name() : null)
                .build();
    }

    private RequirementChangeRiskRequest.RequirementChange toChange(RequirementUpdate u) {
        return RequirementChangeRiskRequest.RequirementChange.builder()
                .revisionNo(u.getRevisionNo())
                .changeType(u.getChangeType().name())
                .previousText(u.getPreviousText())
                .newText(u.getNewText())
                .rationale(u.getRationale())
                .build();
    }

    private RequirementChangeRiskRequest.CommentText toCommentText(String parentType, Comment c) {
        return RequirementChangeRiskRequest.CommentText.builder()
                .parentType(parentType)
                .author(c.getAuthor())
                .text(c.getText())
                .build();
    }
}
