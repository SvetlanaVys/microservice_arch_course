package com.svysk.song_service.controller.error_handler;

import org.springframework.http.HttpStatus;

import java.util.Map;

public record ErrorResponse (
        String errorMessage,
        String errorCode
) {
    public ErrorResponse(String message, HttpStatus status) {
        this(
                message,
                String.valueOf(status.value())
        );
    }
}