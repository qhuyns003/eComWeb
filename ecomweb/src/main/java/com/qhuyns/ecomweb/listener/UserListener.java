package com.qhuyns.ecomweb.listener;

import com.qhuyns.ecomweb.configuration.RabbitMQConfig;
import com.qhuyns.ecomweb.dto.event.ShopCreationFailed;
import com.qhuyns.ecomweb.dto.event.UserSnapshot;
import com.qhuyns.ecomweb.service.UserService;
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
    @RabbitListener(queues = RabbitMQConfig.USER_SERVICE_SHOP_CREATION_FAILED_QUEUE)
    public void handleRollback(ShopCreationFailed shopCreationFailed) {
        if(shopCreationFailed.getUserSnapshot() != null) {
            userService.upgradeSellerRollback(shopCreationFailed.getUserSnapshot());
        }
    }
}
