package com.project.management.risk.prediction.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ai")
public class AiClientProperties {

    private Endpoint overBudget = new Endpoint();
    private Endpoint reqChange = new Endpoint();

    @Data
    public static class Endpoint {
        private String baseUrl;
        private String predictPath = "/predict";
        private long timeoutMs = 5000;
    }
}
