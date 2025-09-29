package com.ecomweb.message_service.entity;

import com.ecomweb.message_service.entity.key.PrivateChatKey;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table("private_chat")
public class PrivateChat {
    @PrimaryKey
    PrivateChatKey key;
    @Column("room_id")
    String roomId;
}