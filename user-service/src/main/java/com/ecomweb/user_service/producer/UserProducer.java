package com.ecomweb.user_service.producer;

import com.ecomweb.user_service.configuration.RabbitMQConfig;
import com.ecomweb.user_service.dto.event.UserCreated;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProducer {

    private final RabbitTemplate rabbitTemplate;
    public void userCreated(UserCreated userCreated) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.USER_EXCHANGE,
                RabbitMQConfig.USER_CREATED,
                userCreated
        );
    }

}

