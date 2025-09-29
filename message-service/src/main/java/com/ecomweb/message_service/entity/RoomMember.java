package com.ecomweb.message_service.entity;

import com.ecomweb.message_service.entity.key.RoomMemberKey;
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
@Table("room_member")
public class RoomMember {
    @PrimaryKey
    private RoomMemberKey key;
    @Column("joined_at")
    private LocalDateTime joinedAt;
}