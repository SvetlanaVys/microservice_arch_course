package com.svysk.song_service.controller.error_handler;

public class SongNotFoundException extends RuntimeException {
    public SongNotFoundException(Long id) {
        super(String.format("Song metadata for ID=%s not found", id));
    }
}
