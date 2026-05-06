package com.project.management.risk.prediction.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@Data
@ConfigurationProperties(prefix = "aggregation")
public class AggregationProperties {

    private Weights weights = new Weights();
    private Thresholds thresholds = new Thresholds();
    private Escalation escalation = new Escalation();

    @Data
    public static class Weights {
        private BigDecimal overBudget = new BigDecimal("0.55");
        private BigDecimal reqChange  = new BigDecimal("0.45");
    }

    @Data
    public static class Thresholds {
        private BigDecimal medium = new BigDecimal("34");
        private BigDecimal high   = new BigDecimal("67");
    }

    @Data
    public static class Escalation {
        private BigDecimal singleScoreFloor = new BigDecimal("80");
    }
}
