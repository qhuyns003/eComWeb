package com.qhuyns.ecomweb.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
