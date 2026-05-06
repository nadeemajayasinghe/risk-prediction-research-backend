package com.project.management.risk.prediction.system.common.exception;

public class AggregationException extends RuntimeException {
    public AggregationException(String message) {
        super(message);
    }

    public AggregationException(String message, Throwable cause) {
        super(message, cause);
    }
}
