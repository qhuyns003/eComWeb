package com.qhuyns.ecomweb.dto.response;

import com.qhuyns.ecomweb.entity.*;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    String id;
    String name;
    String description;
    BigDecimal price;
    String status;
    BigDecimal weight;
    BigDecimal length;
    BigDecimal width;
    BigDecimal height;
    LocalDateTime createdAt;
    CategoryResponse category;
    ShopResponse shop;
    List<ProductImageResponse> images;
    List<ProductVariantResponse> productVariants;
    List<ProductAttributeResponse> productAttributes;
}
