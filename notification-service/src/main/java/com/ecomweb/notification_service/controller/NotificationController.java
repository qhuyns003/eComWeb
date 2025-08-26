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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequestMapping("/notifications")
public class NotificationController {
    NotificationService notificationService;


    @PostMapping("/")
    public ApiResponse<?> createNotification(@RequestBody NotificationRequest notificationRequest) {
        notificationService.createNotification(notificationRequest);
        return ApiResponse.builder()
                .result("Notification created")
                .build();
    }

    @GetMapping("/{userId}")
    public ApiResponse<List<NotificationResponse>> getNotificationsByUser(@PathVariable String userId) {
        return ApiResponse.<List<NotificationResponse>>builder()
                .result(notificationService.getNotificationsByUser(userId))
                .build();
    }



    @PostMapping("/read")
    public ApiResponse<?> markNotificationAsRead(@RequestBody NotificationKeyRequest notificationKeyRequest) {
        notificationService.markNotificationAsRead(notificationKeyRequest);
        return ApiResponse.builder()
                .result("Notification created")
                .build();
    }
}