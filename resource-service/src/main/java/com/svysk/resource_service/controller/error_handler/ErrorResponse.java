package com.svysk.resource_service.controller.error_handler;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
        String errorCode,
        String errorMessage
) {
    public ErrorResponse(HttpStatus status, String message) {
        this(
                String.valueOf(status.value()),
                message
        );
    }
}
