package com.qhuyns.ecomweb.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductFilterRequest {
    BigDecimal minPrice;
    BigDecimal maxPrice;
}
