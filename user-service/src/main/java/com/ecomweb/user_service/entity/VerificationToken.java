package com.ecomweb.user_service.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
@Document(collection = "verification-token")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VerificationToken {
    String id;
    String token;
    LocalDateTime expiryDate;
    String userId;
}
