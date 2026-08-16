package com.svysk.resource_service.controller.error_handler;

public class InvalidResourceIdsFormatException extends RuntimeException {

    public InvalidResourceIdsFormatException(String ids) {
        super(
                String.format("Invalid ID format: '%s'. Only positive integers are allowed", ids)
        );
    }
}