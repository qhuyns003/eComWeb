package com.ecomweb.notification_service.dto.response;


import com.ecomweb.notification_service.entity.key.NotificationKey;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
    private NotificationKeyResponse key;
    private String type;
    private String title;
    private String message;
    private String status;
}
