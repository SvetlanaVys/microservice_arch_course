package com.svysk.resource_processor.adapters.messaging.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ResourceUploadedListener {

    @RabbitListener(queues = "resource.uploaded.queue")
    public void handleResourceUploaded(ResourceUploadedEvent event) {
        Long resourceId = event.resourceId();

        System.out.println("Received resourceId: " + resourceId);
    }

}
