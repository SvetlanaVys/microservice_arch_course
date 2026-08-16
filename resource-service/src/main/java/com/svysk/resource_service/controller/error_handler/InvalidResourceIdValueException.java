package com.svysk.resource_service.controller.error_handler;

public class InvalidResourceIdValueException extends RuntimeException {

    public InvalidResourceIdValueException(Long id) {
        super(
                String.format("Invalid value '%s' for ID. Must be a positive integer", id)
        );
    }
}
