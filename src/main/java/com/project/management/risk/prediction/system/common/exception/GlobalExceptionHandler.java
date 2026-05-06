package com.project.management.risk.prediction.system.common.exception;

import com.project.management.risk.prediction.system.common.api.ApiError;
import com.project.management.risk.prediction.system.common.api.ApiResponse;
import com.project.management.risk.prediction.system.common.api.ErrorCode;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> notFound(ResourceNotFoundException ex) {
        log.info("Not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ErrorCode.RESOURCE_NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Void>> conflict(ConflictException ex) {
        return build(HttpStatus.CONFLICT, ErrorCode.CONFLICT, ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> validation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();
        return build(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR, "Validation failed", details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> constraint(ConstraintViolationException ex) {
        List<String> details = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();
        return build(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR, "Validation failed", details);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> notReadable(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR, "Malformed request body", null);
    }

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<ApiResponse<Void>> circuitOpen(CallNotPermittedException ex) {
        log.warn("Circuit breaker open: {}", ex.getMessage());
        return build(HttpStatus.SERVICE_UNAVAILABLE, ErrorCode.AI_CIRCUIT_OPEN,
                "Upstream AI service circuit is open", null);
    }

    @ExceptionHandler(AiUpstreamException.class)
    public ResponseEntity<ApiResponse<Void>> aiUpstream(AiUpstreamException ex) {
        log.warn("AI upstream error [{} {}]: {}", ex.getModelType(), ex.getKind(), ex.getMessage());
        HttpStatus status;
        String code;
        switch (ex.getKind()) {
            case TIMEOUT, IO_ERROR -> { status = HttpStatus.GATEWAY_TIMEOUT; code = ErrorCode.AI_UPSTREAM_TIMEOUT; }
            case SERVER_ERROR ->     { status = HttpStatus.BAD_GATEWAY;      code = ErrorCode.AI_UPSTREAM_5XX;    }
            case CLIENT_ERROR ->     { status = HttpStatus.BAD_GATEWAY;      code = ErrorCode.AI_UPSTREAM_4XX;    }
            case CIRCUIT_OPEN ->     { status = HttpStatus.SERVICE_UNAVAILABLE; code = ErrorCode.AI_CIRCUIT_OPEN; }
            default ->                { status = HttpStatus.BAD_GATEWAY;      code = ErrorCode.AI_UPSTREAM_5XX;    }
        }
        return build(status, code, ex.getMessage(), null);
    }

    @ExceptionHandler(AggregationException.class)
    public ResponseEntity<ApiResponse<Void>> aggregation(AggregationException ex) {
        log.error("Aggregation failed", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.AGGREGATION_FAILED, ex.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> generic(Exception ex) {
        log.error("Unhandled exception", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_ERROR,
                "An unexpected error occurred", null);
    }

    private ResponseEntity<ApiResponse<Void>> build(HttpStatus status, String code, String message, List<String> details) {
        ApiError error = ApiError.builder()
                .code(code)
                .message(message)
                .details(details)
                .traceId(MDC.get("traceId"))
                .build();
        return ResponseEntity.status(status).body(ApiResponse.error(error));
    }
}
