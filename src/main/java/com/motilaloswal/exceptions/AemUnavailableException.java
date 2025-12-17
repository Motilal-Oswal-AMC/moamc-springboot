package com.motilaloswal.exceptions;

/**
 * Represents a failure to connect to the AEM service (e.g., connection refused, timeout).
 */
public class AemUnavailableException extends RuntimeException {

    public AemUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}