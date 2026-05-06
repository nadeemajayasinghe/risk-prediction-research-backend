package com.project.management.risk.prediction.system.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({AiClientProperties.class, AggregationProperties.class})
public class PropertiesConfig {
}
