package com.qhuyns.ecomweb.mapper;

import com.qhuyns.ecomweb.dto.response.CouponResponse;
import com.qhuyns.ecomweb.entity.Coupon;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CouponMapper {

    CouponResponse toCouponResponse(Coupon coupon);



}
