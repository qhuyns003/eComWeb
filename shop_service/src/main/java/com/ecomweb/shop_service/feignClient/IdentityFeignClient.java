package com.ecomweb.shop_service.feignClient;

import com.ecomweb.shop_service.configuration.FeignConfig;
import com.ecomweb.shop_service.dto.request.IntrospectRequest;
import com.ecomweb.shop_service.dto.request.UpgradeSellerRequest;
import com.ecomweb.shop_service.dto.response.ApiResponse;
import com.ecomweb.shop_service.dto.response.IntrospectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "identity-service", url = "http://localhost:8084/identity", configuration = FeignConfig.class)
public interface IdentityFeignClient {
    @PutMapping("/users/toSeller")
    ApiResponse<?> upgradeToSeller(@RequestBody UpgradeSellerRequest request);

    @GetMapping("/users/byUsername/{username}")
    ApiResponse<?> getUserId(@PathVariable String username);

    @PostMapping("/auth/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request);

}
