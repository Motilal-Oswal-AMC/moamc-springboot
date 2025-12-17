package com.motilaloswal.exceptions;

import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Endpoint to get the full Content Fragment model data.
     * URL: GET /api/content/full
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "invalid_argument",
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Endpoint to get the filtered Content Fragment model data.
     * URL: GET /api/content/filtered
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        // ADD THIS LINE to see the full Redis stack trace in your console/logs
        LOG.error("Runtime exception encountered: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "internal_error",
                "An internal server error occurred: " + ex.getMessage(), // Be careful exposing internal messages in Prod
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles errors returned *from* AEM (e.g., 404 Not Found, 500 Internal Error).
     * Returns the exact status and body that AEM provided.
     */
    @ExceptionHandler(AemClientException.class)
    public ResponseEntity<ErrorResponse> handleAemClientException(AemClientException ex) {
        LOG.warn("AEM client error encountered: {} - {}", ex.getStatusCode(), ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getStatusCode().value(),
                "aem_client_error",
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, ex.getStatusCode());
    }

    /**
     * Handles connection failures *to* AEM (e.g., Connection Refused, Timeout).
     * Returns a 503 Service Unavailable.
     */
    @ExceptionHandler(AemUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleAemUnavailableException(AemUnavailableException ex) {
        LOG.error("AEM service is unavailable: {}", ex.getMessage(), ex.getCause());
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "aem_unavailable", // or fallback_failed
                "Could not connect to AEM service.",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * Handles all other unexpected internal errors.
     * Returns a 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        LOG.error("Unexpected internal server error: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "internal_error",
                "An unexpected error occurred.",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles Redis connection failures.
     * Returns a 503 Service Unavailable.
     */
    @ExceptionHandler(org.springframework.data.redis.RedisConnectionFailureException.class)
    public ResponseEntity<ErrorResponse> handleRedisException(Exception ex) {
        LOG.error("Redis connection failed", ex);
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE.value(), // 503 is more appropriate for DB down
                "service_unavailable",
                "Cache service is currently unavailable. Please try again later.",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }
}