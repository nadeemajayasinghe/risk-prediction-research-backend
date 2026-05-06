package com.project.management.risk.prediction.system.reporting.dto;

public record RiskDistribution(
        long low,
        long medium,
        long high,
        long unknown,
        long total
) {}
