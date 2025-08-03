package com.qhuyns.ecomweb.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequest {
   String userAddressId;
   String payment;
   BigDecimal total;
   BigDecimal subtotal;
   BigDecimal shippingFee;
   BigDecimal totalDiscount;
   List<String> couponIds;
   List<OrderShopGroupRequest>  orderShopGroups;

}