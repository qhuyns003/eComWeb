package com.ecomweb.user_service.listener;

import com.ecomweb.user_service.configuration.RabbitMQConfig;
import com.ecomweb.user_service.dto.event.ShopCreationFailed;
import com.ecomweb.user_service.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserListener {
    UserService userService;

// se bi lap vo han cho den khi message trong queue dc xu li
    @RabbitListener(queues = RabbitMQConfig.USER_SERVICE_SHOP_CREATION_FAILED_QUEUE, containerFactory ="rabbitListenerContainerFactory" )
    public void handleRollback(ShopCreationFailed shopCreationFailed) {

        if(shopCreationFailed.getUserSnapshot() != null) {
            userService.upgradeSellerRollback(shopCreationFailed.getUserSnapshot());

        }

    }

}
