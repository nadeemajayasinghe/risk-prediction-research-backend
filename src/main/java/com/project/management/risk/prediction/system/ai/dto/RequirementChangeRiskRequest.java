package com.project.management.risk.prediction.system.ai.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * Outbound payload to the Requirement Change Risk LLM service.
 */
@Data
@Builder
public class RequirementChangeRiskRequest {
    private UUID sprintId;
    private List<StoryText> stories;
    private List<RequirementChange> requirementChanges;
    private List<CommentText> comments;

    @Data
    @Builder
    public static class StoryText {
        private UUID id;
        private String externalKey;
        private String title;
        private String description;
        private String status;
    }

    @Data
    @Builder
    public static class RequirementChange {
        private Integer revisionNo;
        private String changeType;
        private String previousText;
        private String newText;
        private String rationale;
    }

    @Data
    @Builder
    public static class CommentText {
        private String parentType;
        private String author;
        private String text;
    }
}
