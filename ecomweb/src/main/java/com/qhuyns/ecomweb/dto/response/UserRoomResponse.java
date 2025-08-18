package com.qhuyns.ecomweb.dto.response;

import com.qhuyns.ecomweb.entity.key.UserRoomKey;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRoomResponse {
    private UserRoomKeyResponse key;
    private LocalDateTime joinedAt;
    String roomName;

}
