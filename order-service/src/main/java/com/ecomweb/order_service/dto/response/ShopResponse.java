package com.qhuyns.ecomweb.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShopResponse {

    String id;
    String name;
    String description;
    String address;
    String status;
    LocalDateTime createdAt;
    ShopAddressResponse shopAddressResponse;

}
