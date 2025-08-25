package com.ecomweb.notification_service.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationKeyResponse {
    private String userId;
    private LocalDateTime createdAt;
    private String notificationId;
}
