package com.svysk.resource_service.adapters.messaging.rabbitmq;

import com.svysk.resource_service.application.port.out.ResourceEventPublisher;
import com.svysk.resource_service.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class ResourceMessagePublisher implements ResourceEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public ResourceMessagePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishResourceUploaded(Long resourceId) {

        ResourceUploadedEvent event = new ResourceUploadedEvent(resourceId);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.RESOURCE_EXCHANGE,
                RabbitMQConfig.RESOURCE_ROUTING_KEY,
                event
        );
    }
}
