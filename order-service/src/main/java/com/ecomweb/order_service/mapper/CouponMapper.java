package com.ecomweb.order_service.mapper;

import com.ecomweb.order_service.dto.response.CouponResponse;
import com.ecomweb.order_service.entity.Coupon;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CouponMapper {

    CouponResponse toCouponResponse(Coupon coupon);



}
