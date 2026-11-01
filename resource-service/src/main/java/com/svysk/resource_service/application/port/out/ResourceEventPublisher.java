package com.svysk.resource_service.application.port.out;

public interface ResourceEventPublisher {

    void publishResourceUploaded(Long resourceId);
}
