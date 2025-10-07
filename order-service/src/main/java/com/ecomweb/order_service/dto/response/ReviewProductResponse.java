package com.ecomweb.order_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewProductResponse {
    String id;
    int rating;
    String comment;
    LocalDateTime createdAt;
    String userId;
    ProductVariantResponse productVariant;
}
