package com.ecomweb.identity_service.dto.event;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShopCreationFailed {
    UserSnapshot userSnapshot;
}
