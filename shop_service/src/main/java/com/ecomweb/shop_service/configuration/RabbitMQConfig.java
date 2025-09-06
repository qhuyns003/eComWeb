package com.ecomweb.shop_service.configuration;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String USER_EXCHANGE = "userExchange";
    public static final String ROLLBACK_ROUTING_KEY = "rollbackApi";

    // === Queues ===
    public static final String ROLLBACK_TOSELLER_QUEUE = "order.rollbackSellerQueue";

    // === Routing Keys ===
    public static final String ROLLBACK_TOSELLER = "order.rollbackSeller";

    @Bean
    public DirectExchange userExchange() {
        return new DirectExchange(USER_EXCHANGE);
    }

    @Bean
    public Queue rollbackToSellerQueue() {
        return QueueBuilder.durable(ROLLBACK_TOSELLER_QUEUE).build();
    }

    // dua vao ten tham so de lay ra bean queue va exchange tuogn ung
    @Bean
    public Binding bindingRollback(Queue rollbackToSellerQueue, DirectExchange userExchange) {
        return BindingBuilder.bind(rollbackToSellerQueue)
                .to(userExchange)
                .with(ROLLBACK_TOSELLER);
    }
}

