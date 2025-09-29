package com.ecomweb.message_service.entity;

import com.ecomweb.message_service.entity.key.MessagePrimaryKey;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
// Message.java
// table nay cua cassandra kp cua jkt
@Table("message")
public class Message {
    @PrimaryKey
    private MessagePrimaryKey key;
    private String sender;
    private String content;
    private String type;
    String sendername;
}