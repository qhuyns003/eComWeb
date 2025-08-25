package com.ecomweb.notification_service.dto.request;

import com.ecomweb.notification_service.entity.key.NotificationKey;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationRequest {
    NotificationKeyRequest key;
    private String type;
    private String title;
    private String message;
    private String status;
}
