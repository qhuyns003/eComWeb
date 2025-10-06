package com.qhuyns.ecomweb.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageResponse {
    private MessagePrimaryKeyResponse key;
    private String sender;
    private String content;
    private String type;
    String sendername;
}
