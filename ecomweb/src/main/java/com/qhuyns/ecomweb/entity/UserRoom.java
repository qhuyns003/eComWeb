package com.qhuyns.ecomweb.entity;

//import jakarta.persistence.*;
import com.qhuyns.ecomweb.entity.key.UserRoomKey;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
// jpa co the k can table nma csd thi can vi no k giup tao db
@Table("user_room")
public class UserRoom {
    @PrimaryKey
    private UserRoomKey key;
    // khong tu doi camelCase sang snakeCase nhu jpa
    @Column("joined_at")
    private LocalDateTime joinedAt;

    @Column("room_name")
    String roomName;
}