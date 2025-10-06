package com.qhuyns.ecomweb.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessagePrimaryKeyRequest {
    private String roomId;

    private LocalDateTime sentAt;

    private String messageId;

}
