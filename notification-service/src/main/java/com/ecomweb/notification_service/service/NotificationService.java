package com.ecomweb.notification_service.service;

import com.ecomweb.notification_service.dto.request.NotificationKeyRequest;
import com.ecomweb.notification_service.dto.request.NotificationRequest;
import com.ecomweb.notification_service.dto.response.NotificationResponse;
import com.ecomweb.notification_service.entity.Notification;
import com.ecomweb.notification_service.entity.NotificationStatus;
import com.ecomweb.notification_service.mapper.NotificationKeyMapper;
import com.ecomweb.notification_service.mapper.NotificationMapper;
import com.ecomweb.notification_service.repository.NotificationRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    // Tạo thông báo mới
    public void createNotification(NotificationRequest notificationRequest) {
        Notification notification = notificationMapper.toNotification(notificationRequest);
        notification.setKey(notificationKeyMapper.toNotificationKey(notificationRequest.getKey()));
        notification.setStatus(NotificationStatus.UNREAD.name());
        notificationRepository.save(notification);
    }

    // Lấy tất cả thông báo của một user
    public List<NotificationResponse> getNotificationsByUser(String userId) {
        return notificationRepository.findByKeyUserIdOrderByKeyCreatedAtDesc(userId)
                .stream().map((notification) -> {
                    NotificationResponse notificationResponse = notificationMapper.toNotificationResponse(notification);
                    notificationResponse.setKey(notificationKeyMapper.toNotificationKeyResponse(notification.getKey()));
                    return notificationResponse;
                }).collect(Collectors.toList());
    }

    // Đánh dấu thông báo là đã đọc
    public void markNotificationAsRead(NotificationKeyRequest notificationKeyRequest) {
        Notification notification = notificationRepository
                .findByKeyUserIdAndKeyCreatedAtAndKeyNotificationId(notificationKeyRequest.getUserId(),notificationKeyRequest.getCreatedAt(),notificationKeyRequest.getNotificationId());
        notification.setStatus(NotificationStatus.READ.name());
        notificationRepository.save(notification);
    }
}