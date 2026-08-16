package com.svysk.song_service.controller.error_handler;

public class InvalidRecordIdValueException extends RuntimeException {
    public InvalidRecordIdValueException(Long id) {
        super(
                String.format("Invalid value '%s' for ID. Must be a positive integer", id)
        );
    }
}
