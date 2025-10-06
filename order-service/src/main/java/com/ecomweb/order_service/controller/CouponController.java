package com.ecomweb.order_service.controller;

import com.ecomweb.order_service.dto.response.ApiResponse;
import com.ecomweb.order_service.dto.response.CouponResponse;
import com.ecomweb.order_service.service.CouponService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CouponController {

    CouponService couponService;

    @GetMapping("/shop")
    ApiResponse<List<CouponResponse>> getByShopId(@RequestParam String shopId){
        return ApiResponse.<List<CouponResponse>>builder()
                .result(couponService.getByShopId(shopId))
                .build();
    }
    @GetMapping("/user")
    ApiResponse<List<CouponResponse>> getByUserId(){
        return ApiResponse.<List<CouponResponse>>builder()
                .result(couponService.getByUserId())
                .build();
    }
}
