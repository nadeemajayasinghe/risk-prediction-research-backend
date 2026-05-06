package com.project.management.risk.prediction.system.ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Loose external response shape — both Python services are expected to
 * return at least riskScore and riskLevel. Anything else is best-effort.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalModelResponse {
    private BigDecimal riskScore;
    private String riskLevel;
    private BigDecimal probability;
    private String explanation;
    private String modelVersion;
}
