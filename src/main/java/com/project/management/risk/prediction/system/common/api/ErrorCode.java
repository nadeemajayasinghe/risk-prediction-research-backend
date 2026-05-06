package com.project.management.risk.prediction.system.common.api;

public final class ErrorCode {
    private ErrorCode() {}

    public static final String VALIDATION_ERROR     = "VALIDATION_ERROR";
    public static final String RESOURCE_NOT_FOUND   = "RESOURCE_NOT_FOUND";
    public static final String CONFLICT             = "CONFLICT";
    public static final String AI_UPSTREAM_TIMEOUT  = "AI_UPSTREAM_TIMEOUT";
    public static final String AI_UPSTREAM_5XX      = "AI_UPSTREAM_5XX";
    public static final String AI_UPSTREAM_4XX      = "AI_UPSTREAM_4XX";
    public static final String AI_CIRCUIT_OPEN      = "AI_CIRCUIT_OPEN";
    public static final String AGGREGATION_FAILED   = "AGGREGATION_FAILED";
    public static final String UNAUTHORIZED         = "UNAUTHORIZED";
    public static final String FORBIDDEN            = "FORBIDDEN";
    public static final String INTERNAL_ERROR       = "INTERNAL_ERROR";
}
