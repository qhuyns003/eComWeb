package com.ecomweb.message_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageRequest {
    MessagePrimaryKeyRequest key;
    String sender;
    String content;
    String type;

}
