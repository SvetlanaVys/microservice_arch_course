package com.svysk.resource_service.adapters.messaging.rabbitmq;

import com.svysk.resource_service.application.port.out.ResourceEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceMessagePublisher implements ResourceEventPublisher {

    private final StreamBridge streamBridge;

    @Override
    public void publishResourceUploaded(Long resourceId) {

        streamBridge.send(
                "resourceUploaded-out-0",
                new ResourceUploadedEvent(resourceId)
        );
    }
}
