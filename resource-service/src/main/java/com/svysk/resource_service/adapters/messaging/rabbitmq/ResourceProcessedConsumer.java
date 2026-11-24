package com.svysk.resource_service.adapters.messaging.rabbitmq;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.svysk.resource_service.domain.service.ResourceService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ResourceProcessedConsumer {

    private final ResourceService resourceService;

    @Bean
    public Consumer<ResourceProcessedEvent> resourceProcessed() {
        return event ->
                resourceService.handleResourceProcessed(event.resourceId());
    }
}
