package com.project.management.risk.prediction.system.ai.client;

import com.project.management.risk.prediction.system.ai.dto.ExternalModelResponse;
import com.project.management.risk.prediction.system.ai.dto.ModelPredictionResponse;
import com.project.management.risk.prediction.system.ai.dto.OverBudgetRiskRequest;
import com.project.management.risk.prediction.system.ai.mapper.ExternalResponseMapper;
import com.project.management.risk.prediction.system.common.api.ModelType;
import com.project.management.risk.prediction.system.common.api.RiskLevel;
import com.project.management.risk.prediction.system.common.exception.AiUpstreamException;
import com.project.management.risk.prediction.system.config.AiClientProperties;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class OverBudgetRiskClient implements RiskModelClient<OverBudgetRiskRequest> {

    private static final String CB_NAME = "overBudgetClient";
    private static final String RETRY_NAME = "overBudgetClient";

    private final WebClient webClient;
    private final AiClientProperties props;
    private final ExternalResponseMapper mapper;
    private final AiAuditWriter auditWriter;

    public OverBudgetRiskClient(@Qualifier("overBudgetWebClient") WebClient webClient,
                                AiClientProperties props,
                                ExternalResponseMapper mapper,
                                AiAuditWriter auditWriter) {
        this.webClient = webClient;
        this.props = props;
        this.mapper = mapper;
        this.auditWriter = auditWriter;
    }

    @Override
    @Retry(name = RETRY_NAME, fallbackMethod = "fallback")
    @CircuitBreaker(name = CB_NAME, fallbackMethod = "fallback")
    public ModelPredictionResponse predict(OverBudgetRiskRequest request) {
        long start = System.currentTimeMillis();
        try {
            ExternalModelResponse external = webClient.post()
                    .uri(props.getOverBudget().getPredictPath())
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ExternalModelResponse.class)
                    .block();

            long latency = System.currentTimeMillis() - start;
            auditWriter.record(request.getSprintId(), ModelType.OVER_BUDGET,
                    request, external, 200, latency, null, 1);
            return mapper.toInternal(ModelType.OVER_BUDGET, external, latency);

        } catch (WebClientResponseException ex) {
            long latency = System.currentTimeMillis() - start;
            auditWriter.record(request.getSprintId(), ModelType.OVER_BUDGET,
                    request, ex.getResponseBodyAsString(),
                    ex.getStatusCode().value(), latency, ex.getMessage(), 1);
            AiUpstreamException.Kind kind = ex.getStatusCode().is5xxServerError()
                    ? AiUpstreamException.Kind.SERVER_ERROR
                    : AiUpstreamException.Kind.CLIENT_ERROR;
            throw new AiUpstreamException(ModelType.OVER_BUDGET, kind,
                    ex.getStatusCode().value(), "Over-budget API error", ex);

        } catch (Exception ex) {
            long latency = System.currentTimeMillis() - start;
            auditWriter.record(request.getSprintId(), ModelType.OVER_BUDGET,
                    request, null, null, latency, ex.getMessage(), 1);
            AiUpstreamException.Kind kind = (ex instanceof TimeoutException) ? AiUpstreamException.Kind.TIMEOUT
                    : (ex instanceof IOException) ? AiUpstreamException.Kind.IO_ERROR
                    : AiUpstreamException.Kind.UNKNOWN;
            throw new AiUpstreamException(ModelType.OVER_BUDGET, kind, null,
                    "Over-budget API call failed", ex);
        }
    }

    @SuppressWarnings("unused")
    private ModelPredictionResponse fallback(OverBudgetRiskRequest request, Throwable t) {
        log.warn("Over-budget model fallback engaged: {}", t.toString());
        return ModelPredictionResponse.builder()
                .modelType(ModelType.OVER_BUDGET)
                .riskScore(BigDecimal.ZERO)
                .riskLevel(RiskLevel.UNKNOWN)
                .explanation("Over-budget model unavailable: " + t.getMessage())
                .modelVersion("fallback")
                .latencyMs(0)
                .degraded(true)
                .build();
    }
}
