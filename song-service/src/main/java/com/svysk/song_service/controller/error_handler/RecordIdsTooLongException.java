package com.svysk.song_service.controller.error_handler;

public class RecordIdsTooLongException extends RuntimeException {
    public RecordIdsTooLongException(int length) {
        super(
                String.format("CSV string is too long: received %s characters, maximum allowed is 200", length)
        );
    }
}
