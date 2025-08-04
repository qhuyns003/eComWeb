package com.qhuyns.ecomweb.dto.response;

import com.qhuyns.ecomweb.dto.request.OrderShopGroupRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    String id;
    String userAddressId;
    String payment;
    BigDecimal total;
    BigDecimal subtotal;
    BigDecimal shippingFee;
    BigDecimal totalDiscount;

}
