package com.ecomweb.shop_service.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    // exchange va key dat theo publisher
    // queue dat theo consummer

    public static final String SHOP_EXCHANGE = "shop.exchange";
    public static final String DLQ_EXCHANGE = "dlq.exchange";

    // === Queues === producer k quan tam queue
    public static final String USER_SERVICE_SHOP_CREATION_FAILED_QUEUE = "user-service.shop-creation-failed.queue";
    public static final String DLQ_QUEUE = "dlq.queue";
    // === Routing Keys ===
    public static final String SHOP_CREATION_FAILED = "shop.creation.failed";
    public static final String DLQ = "dlq";




    // Queue DLQ
    @Bean
    Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ).build();
    }

    @Bean
    public Queue userServiceShopCreationFailedQueue() {

        return QueueBuilder.durable(USER_SERVICE_SHOP_CREATION_FAILED_QUEUE)
                .withArgument("x-dead-letter-exchange", DLQ_EXCHANGE ) // exchange mặc định
                .withArgument("x-dead-letter-routing-key", DLQ) // đẩy sang DLQ khi fail
                .build();
    }

    // Exchange
    @Bean
    public DirectExchange shopExchange() {
        return new DirectExchange(SHOP_EXCHANGE);
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(DLQ_EXCHANGE);
    }

    // dua vao ten tham so de lay ra bean queue va exchange tuogn ung
    // Binding
    @Bean
    Binding binding(Queue deadLetterQueue, DirectExchange exchange) {
        return BindingBuilder.bind(deadLetterQueue).to(exchange).with(DLQ);
    }

    @Bean
    public Binding bindingShopCreationFailed(Queue userServiceShopCreationFailedQueue, DirectExchange shopExchange) {
        return BindingBuilder.bind(userServiceShopCreationFailedQueue)
                .to(shopExchange)
                .with(SHOP_CREATION_FAILED);
    }



    // cau hinh giup serilizable vaf deseriliazlbe object thanh json
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}

