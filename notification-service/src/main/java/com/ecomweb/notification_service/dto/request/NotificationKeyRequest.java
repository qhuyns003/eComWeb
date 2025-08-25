package com.ecomweb.notification_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationKeyRequest {
    private String userId;
    private LocalDateTime createdAt;
    private String notificationId;
}
