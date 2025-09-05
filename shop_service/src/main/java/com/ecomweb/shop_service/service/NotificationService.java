//package com.ecomweb.shop_service.service;
//
//
//import com.ecomweb.shop_service.dto.request.NotificationKeyRequest;
//import com.ecomweb.shop_service.dto.request.NotificationRequest;
//import com.ecomweb.shop_service.dto.response.ApiResponse;
//import com.ecomweb.shop_service.dto.response.NotificationResponse;
//import com.ecomweb.shop_service.dto.response.UserResponse;
//import com.ecomweb.shop_service.entity.Notification;
//import com.ecomweb.shop_service.entity.NotificationStatus;
//import com.ecomweb.shop_service.mapper.NotificationKeyMapper;
//import com.ecomweb.shop_service.mapper.NotificationMapper;
//import com.ecomweb.shop_service.repository.NotificationRepository;
//import com.ecomweb.shop_service.util.AuthUtil;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.reactive.function.client.WebClient;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//// truong hop B response loi cho A thi A phai format lai
////} catch (WebClientResponseException ex) {
////    return ResponseEntity
////            .status(ex.getRawStatusCode())
////            .body(ex.getResponseBodyAsString()); // forward lỗi gốc
////}
//
//@Transactional
//@Service
//@RequiredArgsConstructor
//@Slf4j
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//public class NotificationService {
//    NotificationRepository notificationRepository;
//    NotificationMapper notificationMapper;
//    NotificationKeyMapper notificationKeyMapper;
//    SimpMessagingTemplate messagingTemplate;
//    @Qualifier("mainService")
//    WebClient webClient;
//    // Tạo thông báo mới
//    public void createNotification(NotificationRequest notificationRequest) {
//        for (String userId : notificationRequest.getRecipientId()) {
//            Notification notification = notificationMapper.toNotification(notificationRequest);
//            notification.getKey().setNotificationId(UUID.randomUUID().toString());
//            notification.getKey().setCreatedAt(LocalDateTime.now());
//            notification.getKey().setUserId(userId);
//            notification.setStatus(NotificationStatus.UNREAD.name());
//            notificationRepository.save(notification);
//
//            JwtAuthenticationToken authentication =
//                    (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
//
//            String token = authentication.getToken().getTokenValue();
//
//            String username = webClient.get()
//                    .uri("/users/"+userId)
//                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//                    .retrieve()// gui rq
//                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<UserResponse>>() {})
//                            .block().getResult().getUsername();
//            NotificationResponse notificationResponse = notificationMapper.toNotificationResponse(notification);
//            notificationResponse.setKey(notificationKeyMapper.toNotificationKeyResponse(notification.getKey()));
//            messagingTemplate.convertAndSendToUser(
//                    username,
//                    "/queue/notifications",
//                    ApiResponse.builder()
//                            .result(notificationResponse)
//                            .build()
//            );
//        }
//
//    }
//
//    // Lấy tất cả thông báo của một user
//    public List<NotificationResponse> getNotificationsByUser(String userId) {
//
//        return notificationRepository.findByKeyUserIdOrderByKeyCreatedAtDesc(userId)
//                .stream().map((notification) -> {
//                    NotificationResponse notificationResponse = notificationMapper.toNotificationResponse(notification);
//                    notificationResponse.setKey(notificationKeyMapper.toNotificationKeyResponse(notification.getKey()));
//                    return notificationResponse;
//                }).collect(Collectors.toList());
//    }
//
//    // Đánh dấu thông báo là đã đọc
//    public void markNotificationAsRead(NotificationKeyRequest notificationKeyRequest) {
//        Notification notification = notificationRepository
//                .findByKeyUserIdAndKeyCreatedAtAndKeyNotificationId(notificationKeyRequest.getUserId(),notificationKeyRequest.getCreatedAt(),notificationKeyRequest.getNotificationId());
//        notification.setStatus(NotificationStatus.READ.name());
//        notificationRepository.save(notification);
//
//        String token = AuthUtil.getToken();
//
//        String username = webClient.get()
//                .uri("/users/"+notificationKeyRequest.getUserId())
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token )
//                .retrieve()// gui rq
//                .bodyToMono(new ParameterizedTypeReference<ApiResponse<UserResponse>>() {})
//                .block().getResult().getUsername();
//
//        messagingTemplate.convertAndSendToUser(
//                username,
//                "/queue/notifications",
//                notificationMapper.toNotificationResponse(notification)
//        );
//    }
//}