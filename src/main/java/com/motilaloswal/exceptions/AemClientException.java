package com.motilaloswal.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Represents an HTTP error (4xx or 5xx) returned *from* the AEM service.
 */
public class AemClientException extends RuntimeException {
    private int status;
    private final HttpStatusCode statusCode;
    private final String responseBody;
    private long timestamp;

    public AemClientException(String message, HttpStatusCode statusCode, String responseBody, Throwable cause) {
        super(message, cause);
        this.status = statusCode.value();
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.timestamp = System.currentTimeMillis();
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }


}