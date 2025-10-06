package com.ecomweb.order_service.feignClient;

import com.ecomweb.order_service.configuration.FeignConfig;
import com.ecomweb.order_service.dto.request.IntrospectRequest;
import com.ecomweb.order_service.dto.response.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "identity-service", url = "http://localhost:8084/identity", configuration = FeignConfig.class)
public interface IdentityFeignClient {

    @PostMapping("/auth/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request);

    @GetMapping("/users/activated/{userId}")
    ApiResponse<UserResponse> getActivatedUser(@PathVariable String userId);

    @GetMapping("/users/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable String userId);

    @GetMapping("/users/activated/byUsername/{username}")
    ApiResponse<UserResponse> getActivatedUserByUsername(@PathVariable String username);

    @GetMapping("/user_address/{id}")
    ApiResponse<UserAddressResponse> getUserAddressById(@PathVariable String id);

    @GetMapping("/users/exists/{id}")
    ApiResponse<Boolean> existsById(@PathVariable String id);

    @GetMapping("/users/byUsername/{username}")
    ApiResponse<String> getUserIdByUsername(@PathVariable String username);

    @GetMapping("/user-coupon/byUsername/{username}")
    ApiResponse<List<UserCouponResponse>> getByUsername(@PathVariable String username);




}
