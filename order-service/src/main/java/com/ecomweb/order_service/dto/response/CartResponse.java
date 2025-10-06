package com.qhuyns.ecomweb.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {
    String id;
    int quantity;

    ShopResponse shop;
    String variantId;
    String productName;
    Long variantPrice;
    String imageUrl;
    BigDecimal weight;
    BigDecimal length;
    BigDecimal width;
    BigDecimal height;
    List<String> detailAttributes;

}
