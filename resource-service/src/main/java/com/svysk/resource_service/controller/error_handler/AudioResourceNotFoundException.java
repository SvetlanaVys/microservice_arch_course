package com.svysk.resource_service.controller.error_handler;

public class AudioResourceNotFoundException extends RuntimeException {

    public AudioResourceNotFoundException(Long id) {
        super(String.format("Resource with ID=%s not found", id));
    }
}
