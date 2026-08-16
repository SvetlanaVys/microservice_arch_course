package com.svysk.song_service.controller.error_handler;

public class SongMetadataAlreadyExists extends RuntimeException {
    public SongMetadataAlreadyExists(Long id) {
        super(
                String.format("Metadata for resource ID=%s already exists", id)
        );
    }
}
