package com.qhuyns.ecomweb.feignClient;

import com.qhuyns.ecomweb.configuration.FeignConfig;
import com.qhuyns.ecomweb.dto.request.ApiResponse;
import com.qhuyns.ecomweb.dto.request.IntrospectRequest;
import com.qhuyns.ecomweb.dto.response.IntrospectResponse;
import com.qhuyns.ecomweb.dto.response.UserAddressResponse;
import com.qhuyns.ecomweb.dto.response.UserCouponResponse;
import com.qhuyns.ecomweb.dto.response.UserResponse;
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

    @GetMapping("/users/activated/buUsername/{username}")
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
