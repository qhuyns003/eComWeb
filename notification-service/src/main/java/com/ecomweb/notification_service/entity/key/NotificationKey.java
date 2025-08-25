package com.ecomweb.notification_service.entity.key;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@PrimaryKeyClass
public class NotificationKey {
    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED)
    private String userId;

    @PrimaryKeyColumn(name = "created_at", ordinal = 1, type = org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED)
    private LocalDateTime createdAt;

    @PrimaryKeyColumn(name = "notification_id", ordinal = 2, type = org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED)
    private String notificationId;


}