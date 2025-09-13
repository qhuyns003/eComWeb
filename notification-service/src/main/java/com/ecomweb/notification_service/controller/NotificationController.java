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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class NotificationController {
    NotificationService notificationService;


    @PostMapping("/send")
    public ResponseEntity<ApiResponse<?>> createNotification(@RequestBody NotificationRequest notificationRequest) {
        ApiResponse<?> apiResponse = notificationService.createNotification(notificationRequest);
        return ResponseEntity.status(apiResponse.getHttpStatus())
                .body(apiResponse);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotificationsByUser(@PathVariable String userId) {
        ApiResponse<List<NotificationResponse>> apiResponse = notificationService.getNotificationsByUser(userId);
        return ResponseEntity.status(apiResponse.getHttpStatus())
                .body(apiResponse);
    }



    @PostMapping("/read")
    public ResponseEntity<ApiResponse<?>> markNotificationAsRead(@RequestBody NotificationKeyRequest notificationKeyRequest) {
        ApiResponse<?> apiResponse = notificationService.markNotificationAsRead(notificationKeyRequest);
        return ResponseEntity.status(apiResponse.getHttpStatus())
                .body(apiResponse);
    }
}