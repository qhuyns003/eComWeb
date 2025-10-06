package com.qhuyns.ecomweb.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDetailResponse {
    String id;
    String name;
    Long price;
    String description;
    BigDecimal weight;
    BigDecimal length;
    BigDecimal width;
    BigDecimal height;
    ShopResponse shop;
    CategoryResponse category;
    List<ProductImageResponse> images;
    List<ProductVariantResponse> productVariants;
    List<ProductAttributeResponse> productAttributes;
    Double rating;
    BigDecimal numberOfOrder;
}


