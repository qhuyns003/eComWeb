package com.ecomweb.order_service.dto.response;

import com.ecomweb.order_service.entity.CouponType;
import com.ecomweb.order_service.entity.DiscountType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CouponResponse {
    String id;
    String code;
    CouponType couponType;
    DiscountType discountType;
    BigDecimal discount;
    BigDecimal minOrder;
    LocalDate startDate;
    LocalDate endDate;
    BigDecimal quantity;
    BigDecimal used;
    boolean active;
    boolean requireCode;
}
