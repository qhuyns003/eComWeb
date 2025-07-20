package com.qhuyns.ecomweb.dto.response;


import com.qhuyns.ecomweb.entity.Category;
import com.qhuyns.ecomweb.entity.ProductImage;
import com.qhuyns.ecomweb.entity.ProductVariant;
import com.qhuyns.ecomweb.entity.Shop;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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


