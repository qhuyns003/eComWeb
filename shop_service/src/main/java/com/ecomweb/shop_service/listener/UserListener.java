//package com.ecomweb.shop_service.listener;
//
//import com.ecomweb.shop_service.configuration.RabbitMQConfig;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Service;
//
//@Service
//public class UserListener {
//
//    @RabbitListener(queues = RabbitMQConfig.ROLLBACK_TOSELLER_QUEUE)
//    public void handleEmail(String message) {
//        System.out.println("[EmailService] " + message);
//    }
//
//
////    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
////    public void handleNotification(String message) {
////        if (message.contains("Created")) {
////            System.out.println("[NotificationService] New order notification: " + message);
////        } else if (message.contains("Canceled")) {
////            System.out.println("[NotificationService] Order canceled notification: " + message);
////        }
////    }
//}
