package com.qhuyns.ecomweb.entity.key;

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
public class RoomMemberKey implements Serializable {
    @PrimaryKeyColumn(name = "room_id", type = PrimaryKeyType.PARTITIONED)
    private String roomId;
    @PrimaryKeyColumn(name = "user_id", type = PrimaryKeyType.CLUSTERED)
    private String userId;
}