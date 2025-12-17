package com.motilaloswal.exceptions;

// You can use Lombok annotations for boilerplate code, or generate them manually.
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor // Creates a constructor with all arguments
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private long timestamp;
}