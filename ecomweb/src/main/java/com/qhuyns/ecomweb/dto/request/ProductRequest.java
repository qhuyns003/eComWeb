package com.qhuyns.ecomweb.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {
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
    CategoryRequest category;
    ShopRequest shop;
    List<ProductImageRequest> images;
    List<ProductVariantRequest> productVariants;
    List<ProductAttributeRequest> productAttributes;
}
