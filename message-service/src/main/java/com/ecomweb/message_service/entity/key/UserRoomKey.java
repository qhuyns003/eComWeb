package com.ecomweb.message_service.entity.key;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@PrimaryKeyClass
public class UserRoomKey implements Serializable {
    @PrimaryKeyColumn(name = "user_id", type = PrimaryKeyType.PARTITIONED)
    private String userId;
    @PrimaryKeyColumn(name = "last_message_at", type = PrimaryKeyType.CLUSTERED, ordinal = 0)
    private LocalDateTime lastMessageAt;
    @PrimaryKeyColumn(name = "room_id", type = PrimaryKeyType.CLUSTERED, ordinal = 1)
    private String roomId;
}