//package com.qhuyns.ecomweb.controller;
//
//
//import com.qhuyns.ecomweb.dto.request.ApiResponse;
//import com.qhuyns.ecomweb.dto.request.NotificationRequest;
//import com.qhuyns.ecomweb.dto.response.CategoryResponse;
//import com.qhuyns.ecomweb.dto.response.NotificationResponse;
//import com.qhuyns.ecomweb.entity.NotificationRecipient;
//import com.qhuyns.ecomweb.service.CategoryService;
//import com.qhuyns.ecomweb.service.NotificationService;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/notifications")
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@Slf4j
//public class NotificationController {
//    private final NotificationService notificationService;
//
//    @PostMapping("/send")
//    public ApiResponse<?> sendNotification(@RequestBody NotificationRequest req) {
//        notificationService.sendNotificationToUsers(req);
//        return ApiResponse.builder()
//                .result("Notification sent successfully")
//                .build();
//    }
//
//    @GetMapping("/user/{userId}")
//    public List<NotificationResponse> getUserNotifications(@PathVariable String userId) {
//        return notificationService.getUserNotifications(userId);
//    }
//
//    @PostMapping("/read/{notificationRecipientId}")
//    public ResponseEntity<?> markAsRead(@PathVariable String notificationRecipientId) {
//        notificationService.markAsRead(notificationRecipientId);
//        return ResponseEntity.ok().build();
//    }
//
//}
