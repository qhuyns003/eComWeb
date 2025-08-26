package com.ecomweb.notification_service.controller;

import com.ecomweb.notification_service.dto.request.NotificationKeyRequest;
import com.ecomweb.notification_service.dto.request.NotificationRequest;
import com.ecomweb.notification_service.dto.response.ApiResponse;
import com.ecomweb.notification_service.dto.response.NotificationResponse;
import com.ecomweb.notification_service.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
// dat trung ten voi rést ksao vi no da co prefix e dua den api socket
@RequestMapping("/notifications")
public class NotificationSocketController {
    NotificationService notificationService;

// dung message broken cho cac api k can nhan ket qua tra ve, chap nhan tre
    // rest cho api can kq tra ve va nhanh chong
    @MessageMapping("/send")
    public ApiResponse<?> createNotification(@RequestBody NotificationRequest notificationRequest) {
        notificationService.createNotification(notificationRequest);
        return ApiResponse.builder()
                .result("Notification created")
                .build();
    }


    @MessageMapping("/read")
    public ApiResponse<?> markNotificationAsRead(@RequestBody NotificationKeyRequest notificationKeyRequest) {
        notificationService.markNotificationAsRead(notificationKeyRequest);
        return ApiResponse.builder()
                .result("Notification created")
                .build();
    }
}