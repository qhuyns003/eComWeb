package com.qhuyns.ecomweb.service;

import com.qhuyns.ecomweb.dto.request.NotificationRequest;
import com.qhuyns.ecomweb.dto.response.CategoryResponse;
import com.qhuyns.ecomweb.dto.response.NotificationResponse;
import com.qhuyns.ecomweb.entity.Notification;
import com.qhuyns.ecomweb.entity.NotificationRecipient;
import com.qhuyns.ecomweb.entity.User;
import com.qhuyns.ecomweb.exception.AppException;
import com.qhuyns.ecomweb.exception.ErrorCode;
import com.qhuyns.ecomweb.mapper.CategoryMapper;
import com.qhuyns.ecomweb.mapper.NotificationMapper;
import com.qhuyns.ecomweb.repository.CategoryRepository;
import com.qhuyns.ecomweb.repository.NotificationRecipientRepository;
import com.qhuyns.ecomweb.repository.NotificationRepository;
import com.qhuyns.ecomweb.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository notificationRecipientRepository;
    UserRepository userRepository;
    NotificationMapper notificationMapper;

    public void sendNotificationToUsers(NotificationRequest notificationRequest) {
        User sender = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXISTED));
        List<User> recipients = userRepository.findAllById(notificationRequest.getRecipientId());

        Notification notification = Notification.builder()
                .title(notificationRequest.getTitle())
                .content(notificationRequest.getContent())
                .sender(sender)
                .build();
        notificationRepository.save(notification);

        for (User user : recipients) {
            NotificationRecipient nr = NotificationRecipient.builder()
                    .notification(notification)
                    .recipient(user)
                    .seen(false)
                    .build();
            notificationRecipientRepository.save(nr);

            // Log username gửi WebSocket
            log.info("Send WebSocket to user: {}", user.getUsername());
            // Gửi realtime qua WebSocket tới user
            messagingTemplate.convertAndSendToUser(
                    user.getUsername(),
                    "/queue/notifications",
                    notificationMapper.toNotificationResponse(notification)
            );
        }
    }

    public List<NotificationResponse> getUserNotifications(String userId) {
        List<NotificationRecipient> notificationRecipient = notificationRecipientRepository.findByRecipientId(userId);

        List<NotificationResponse> notificationResponses= notificationRecipient.stream().map(nr -> notificationMapper.toNotificationResponse(nr.getNotification()))
                .collect(Collectors.toList());
        return notificationResponses;
    }

    public void markAsRead(String notificationRecipientId) {
        NotificationRecipient notificationRecipient = notificationRecipientRepository.findById(notificationRecipientId)
                .orElseThrow(()-> new AppException(ErrorCode.NOTIFICATION_RECIPIENT_NOT_EXISTS));
        notificationRecipient.setSeen(true);
        notificationRecipientRepository.save(notificationRecipient);
    }

}