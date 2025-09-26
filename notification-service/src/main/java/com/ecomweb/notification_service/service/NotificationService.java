package com.ecomweb.notification_service.service;

import com.ecomweb.notification_service.dto.request.NotificationKeyRequest;
import com.ecomweb.notification_service.dto.request.NotificationRequest;
import com.ecomweb.notification_service.dto.response.ApiResponse;
import com.ecomweb.notification_service.dto.response.NotificationResponse;
import com.ecomweb.notification_service.entity.Notification;
import com.ecomweb.notification_service.entity.NotificationStatus;
import com.ecomweb.notification_service.feignClient.IdentityFeignClient;
import com.ecomweb.notification_service.mapper.NotificationKeyMapper;
import com.ecomweb.notification_service.mapper.NotificationMapper;
import com.ecomweb.notification_service.repository.NotificationRepository;
import com.ecomweb.notification_service.util.AuthUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
    NotificationRepository notificationRepository;
    NotificationMapper  notificationMapper;
    NotificationKeyMapper  notificationKeyMapper;
    SimpMessagingTemplate messagingTemplate;
    IdentityFeignClient identityFeignClient;
    // chỉ cần 1 lỗi unchecked throw ra, tất cả save sẽ rollback dù trc đó có get (nó sẽ lấy dl cache), hay flush
    @Transactional
    public void createNotification(NotificationRequest notificationRequest) {

        for (String userId : notificationRequest.getRecipientId()) {
            Notification notification = notificationMapper.toNotification(notificationRequest);
            notification.getKey().setNotificationId(UUID.randomUUID().toString());
            notification.getKey().setCreatedAt(LocalDateTime.now());
            notification.getKey().setUserId(userId);
            notification.setStatus(NotificationStatus.UNREAD.name());
            notificationRepository.save(notification);

            String username = identityFeignClient.getUsernameById(userId).getResult().getUsername();

            NotificationResponse notificationResponse = notificationMapper.toNotificationResponse(notification);
            notificationResponse.setKey(notificationKeyMapper.toNotificationKeyResponse(notification.getKey()));
            messagingTemplate.convertAndSendToUser(
                    username,
                    "/queue/notifications",
                    ApiResponse.builder()
                            .result(notificationResponse)
                            .build()
            );
        }

    };

    // Lấy tất cả thông báo của một user
    public List<NotificationResponse> getNotificationsByUser(String userId) {
        List<NotificationResponse> responses = notificationRepository.findByKeyUserIdOrderByKeyCreatedAtDesc(userId)
                .stream().map((notification) -> {
                    NotificationResponse notificationResponse = notificationMapper.toNotificationResponse(notification);
                    notificationResponse.setKey(notificationKeyMapper.toNotificationKeyResponse(notification.getKey()));
                    return notificationResponse;
                }).collect(Collectors.toList());
        return responses;
    }

    // Đánh dấu thông báo là đã đọc
    public void markNotificationAsRead(NotificationKeyRequest notificationKeyRequest) {
        Notification notification = notificationRepository
                .findByKeyUserIdAndKeyCreatedAtAndKeyNotificationId(notificationKeyRequest.getUserId(),notificationKeyRequest.getCreatedAt(),notificationKeyRequest.getNotificationId());
        notification.setStatus(NotificationStatus.READ.name());
        notificationRepository.save(notification);

        String token = AuthUtil.getToken();
        String username = identityFeignClient.getUsernameById(notificationKeyRequest.getUserId()).getResult().getUsername();
        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/notifications",
                notificationMapper.toNotificationResponse(notification)
        );
    }
}