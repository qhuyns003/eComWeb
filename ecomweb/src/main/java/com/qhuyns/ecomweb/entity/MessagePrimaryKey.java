package com.qhuyns.ecomweb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@PrimaryKeyClass
public class MessagePrimaryKey implements Serializable {
    @PrimaryKeyColumn(name = "room_id", type = PrimaryKeyType.PARTITIONED)
    private String roomId;

    @PrimaryKeyColumn(name = "sent_at", type = PrimaryKeyType.CLUSTERED, ordinal = 0)
    private LocalDateTime sentAt;

    @PrimaryKeyColumn(name = "message_id", type = PrimaryKeyType.CLUSTERED, ordinal = 1)
    private UUID messageId;
}