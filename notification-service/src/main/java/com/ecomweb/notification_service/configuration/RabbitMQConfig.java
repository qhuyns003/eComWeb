package com.ecomweb.notification_service.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String USER_EXCHANGE = "userExchange";

    // === Queues ===
    public static final String ROLLBACK_TOSELLER_QUEUE = "user.rollbackSellerQueue";

    // === Routing Keys ===
    public static final String ROLLBACK_TOSELLER = "user.rollbackSeller";

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

