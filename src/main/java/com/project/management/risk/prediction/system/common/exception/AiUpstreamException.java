package com.project.management.risk.prediction.system.common.exception;

import com.project.management.risk.prediction.system.common.api.ModelType;
import lombok.Getter;

@Getter
public class AiUpstreamException extends RuntimeException {
    private final ModelType modelType;
    private final Integer httpStatus;
    private final Kind kind;

    public AiUpstreamException(ModelType modelType, Kind kind, Integer httpStatus, String message, Throwable cause) {
        super(message, cause);
        this.modelType = modelType;
        this.kind = kind;
        this.httpStatus = httpStatus;
    }

    public enum Kind { TIMEOUT, SERVER_ERROR, CLIENT_ERROR, CIRCUIT_OPEN, IO_ERROR, UNKNOWN }
}
