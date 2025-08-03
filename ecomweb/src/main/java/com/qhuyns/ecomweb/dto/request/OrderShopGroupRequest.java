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
public class OrderShopGroupRequest {
    List<String> shopCouponIds;
    BigDecimal shippingFee;
    String shopId;
    BigDecimal total;
    BigDecimal subTotal;
    BigDecimal totalDiscount ;
    List<OrderItemRequest> orderItems;

}
