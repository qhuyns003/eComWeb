package com.qhuyns.ecomweb.entity;

//import jakarta.persistence.*;
import com.qhuyns.ecomweb.entity.key.RoomMemberKey;
import lombok.*;
import lombok.experimental.FieldDefaults;
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
    private LocalDateTime joinedAt;
}