package com.svysk.song_service.controller.error_handler;

import org.springframework.http.HttpStatus;

import java.util.Map;

public record ErrorDetailsResponse(
        String errorMessage,
        Map<String, String> details,
        String errorCode
) {
    public ErrorDetailsResponse(String message, Map<String, String> details, HttpStatus status) {
        this(
                message,
                details,
                String.valueOf(status.value())
        );
    }
}
