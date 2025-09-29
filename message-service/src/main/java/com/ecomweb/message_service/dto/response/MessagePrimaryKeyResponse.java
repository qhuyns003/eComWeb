package com.ecomweb.message_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessagePrimaryKeyResponse {
    private String roomId;
    private LocalDateTime sentAt;
    private UUID messageId;

}
