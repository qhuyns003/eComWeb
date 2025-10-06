package com.ecomweb.order_service.listener;

import com.ecomweb.order_service.configuration.RabbitMQConfig;
import com.ecomweb.order_service.service.EmailService;
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
public class MailListener {
    EmailService emailService;
//    @RabbitListener(queues = RabbitMQConfig.MAIL_SERVICE_USER_CREATED_QUEUE, containerFactory ="rabbitListenerContainerFactory" )
//    public void userCreated(UserCreated userCreated) throws Exception {
//        if(userCreated.getEmailVerificationRequest() != null) {
//            String token = userCreated.getEmailVerificationRequest().getToken();
//            String email = userCreated.getEmailVerificationRequest().getEmail();
//            String username =  userCreated.getEmailVerificationRequest().getUsername();
//            emailService.sendVerificationEmail(email,token,username);
//        }
//
//    }
}
