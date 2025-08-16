package com.qhuyns.ecomweb.dto.response;

import com.qhuyns.ecomweb.entity.key.RoomMemberKey;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomMemberKeyResponse {
    String roomId;
    String userId;

}
