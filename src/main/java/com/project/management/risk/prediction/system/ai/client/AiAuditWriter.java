package com.project.management.risk.prediction.system.ai.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.management.risk.prediction.system.common.api.ModelType;
import com.project.management.risk.prediction.system.risk.entity.AiModelResponseLog;
import com.project.management.risk.prediction.system.risk.repository.AiModelResponseLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Writes one row per AI call (success OR failure) to ai_model_responses,
 * always in a new transaction so audit survives a rollback in the caller.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AiAuditWriter {

    private final AiModelResponseLogRepository repository;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UUID record(UUID sprintId,
                       ModelType modelType,
                       Object requestPayload,
                       Object responsePayload,
                       Integer httpStatus,
                       Long latencyMs,
                       String errorMessage,
                       int attemptCount) {
        AiModelResponseLog row = AiModelResponseLog.builder()
                .sprintId(sprintId)
                .modelType(modelType)
                .requestPayload(serialize(requestPayload))
                .responsePayload(serialize(responsePayload))
                .httpStatus(httpStatus)
                .latencyMs(latencyMs)
                .errorMessage(truncate(errorMessage, 4000))
                .attemptCount(attemptCount)
                .build();
        return repository.save(row).getId();
    }

    private String serialize(Object o) {
        if (o == null) return null;
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize AI payload for audit: {}", e.getMessage());
            return o.toString();
        }
    }

    private String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max);
    }
}
