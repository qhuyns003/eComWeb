package com.ecomweb.identity_service.controller;


import com.ecomweb.identity_service.dto.request.UpgradeSellerRequest;
import com.ecomweb.identity_service.dto.request.UserCreationRequest;
import com.ecomweb.identity_service.dto.request.UserUpdateRequest;
import com.ecomweb.identity_service.dto.response.ApiResponse;
import com.ecomweb.identity_service.dto.response.UserResponse;
import com.ecomweb.identity_service.service.UserCouponService;
import com.ecomweb.identity_service.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-coupon")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserCouponController {
    UserCouponService userCouponService;

    @GetMapping("/byUsername/{username}")
    ApiResponse<?> getByUsername(@PathVariable String username){
        return ApiResponse.builder()
                .result(userCouponService.getByUserName(username))
                .build();
    }


}