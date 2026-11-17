package com.svysk.resource_processor.adapters.messaging.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceMessagePublisher {

    private final StreamBridge streamBridge;

    public void publishResourceProcessed(Long resourceId) {
        streamBridge.send(
                "resourceProcessed-out-0",
                new ResourceProcessedEvent(resourceId)
        );
    }
}
