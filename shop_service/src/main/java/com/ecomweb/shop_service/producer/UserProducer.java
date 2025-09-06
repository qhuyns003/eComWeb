package com.ecomweb.shop_service.producer;

import com.ecomweb.shop_service.configuration.RabbitMQConfig;
import com.ecomweb.shop_service.dto.event.UpgradeToSellerSnapshot;
import com.ecomweb.shop_service.util.RedisCacheHelper;
import com.ecomweb.shop_service.util.RedisKey;
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

    public void rollbackUser(UpgradeToSellerSnapshot upgradeToSellerSnapshot) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.USER_EXCHANGE,
                RabbitMQConfig.ROLLBACK_TOSELLER,
                upgradeToSellerSnapshot
        );
    }

}

