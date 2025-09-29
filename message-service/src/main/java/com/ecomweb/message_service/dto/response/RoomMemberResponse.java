package com.ecomweb.message_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomMemberResponse {
    RoomMemberKeyResponse key;
    LocalDateTime joinedAt;

}
