package com.ecomweb.shop_service.producer;

import com.ecomweb.shop_service.configuration.RabbitMQConfig;
import com.ecomweb.shop_service.dto.event.ShopCreationFailed;
import com.ecomweb.shop_service.dto.event.UserSnapshot;
import com.ecomweb.shop_service.util.RedisCacheHelper;
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
    RedisCacheHelper cacheHelper;
// compensation fail -> push dlq, k xu ly compen cua compen
    public void shopCreationFailed(ShopCreationFailed shopCreationFailed) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SHOP_EXCHANGE,
                RabbitMQConfig.SHOP_CREATION_FAILED,
                shopCreationFailed
        );
    }

}

