package com.svysk.resource_service.controller.error_handler;

public class ResourceIdsTooLongExcepion extends RuntimeException {

    public ResourceIdsTooLongExcepion(int length) {
        super(
                String.format("CSV string is too long: received %s characters, maximum allowed is 200", length)
        );
    }
}
