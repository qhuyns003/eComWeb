package com.qhuyns.ecomweb.dto.response;

import com.qhuyns.ecomweb.entity.*;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductOverviewResponse {
    String id;
    String name;
    BigDecimal price;
    List<ProductImageResponse> images;
    Double rating;
    BigDecimal numberOfOrder;
}
