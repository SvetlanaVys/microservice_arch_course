package com.svysk.song_service.controller.error_handler;

public class InvalidRecordIdsFormatException extends RuntimeException {

    public InvalidRecordIdsFormatException(String ids) {
        super(
                String.format("Invalid ID format: '%s'. Only positive integers are allowed", ids)
        );
    }
}
