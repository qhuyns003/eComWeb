package com.qhuyns.ecomweb.configuration;


import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// k config rabbit mac dinh gui lai khi fail
@Configuration
public class RabbitErrorHandlerConfig {

    @Bean
    public ConditionalRejectingErrorHandler errorHandler() {
        // Error handler mặc định: reject message, không requeue
        return new ConditionalRejectingErrorHandler();
    }
}


