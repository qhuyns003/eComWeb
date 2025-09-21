package com.qhuyns.ecomweb.dto.event;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreated {
    EmailVerificationRequest emailVerificationRequest;
}
