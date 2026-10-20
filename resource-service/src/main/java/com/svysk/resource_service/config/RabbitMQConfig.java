package com.svysk.resource_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    public static final String RESOURCE_EXCHANGE =
            "resource.exchange";

    public static final String RESOURCE_QUEUE =
            "resource.uploaded.queue";

    public static final String RESOURCE_ROUTING_KEY =
            "resource.uploaded";

    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public TopicExchange resourceExchange() {
        return new TopicExchange(RESOURCE_EXCHANGE, true, false);
    }

    @Bean
    public Queue resourceQueue() {
        return QueueBuilder.durable(RESOURCE_QUEUE).build();
    }

    @Bean
    public Binding resourceBinding(
            Queue resourceQueue,
            TopicExchange resourceExchange
    ) {
        return BindingBuilder
                .bind(resourceQueue)
                .to(resourceExchange)
                .with(RESOURCE_ROUTING_KEY);
    }

    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }


/*    @Bean
    ApplicationRunner rabbitMQDiagnostic(RabbitTemplate rabbitTemplate) {
        return args -> {

            System.out.println("RabbitMQ diagnostic started");

            try {
                rabbitTemplate
                        .execute(channel -> {
                            System.out.println("RabbitMQ connection established: "
                            + channel.isOpen());
                            return null;
                        });
            } catch (Exception e) {
                System.err.println("RabbitMQ connection failed " + e.getMessage());
                e.printStackTrace();
            }
        };
    }*/
}
