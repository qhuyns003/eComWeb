package com.qhuyns.ecomweb.dto.response;

import com.qhuyns.ecomweb.entity.key.RoomMemberKey;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomMemberResponse {
    RoomMemberKeyResponse key;
    LocalDateTime joinedAt;

}
