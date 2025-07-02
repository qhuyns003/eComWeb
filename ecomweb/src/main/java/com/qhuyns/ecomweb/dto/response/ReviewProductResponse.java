package com.qhuyns.ecomweb.dto.response;

import com.qhuyns.ecomweb.entity.Cart;
import com.qhuyns.ecomweb.entity.OrderItem;
import com.qhuyns.ecomweb.entity.Product;
import com.qhuyns.ecomweb.entity.ProductAttribute;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

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
    UserResponse user;
    ProductVariantResponse productVariant;
}
