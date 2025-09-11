package com.ecomweb.shop_service.feignClient;

import com.ecomweb.shop_service.configuration.FeignConfig;
import com.ecomweb.shop_service.dto.request.UpgradeSellerRequest;
import com.ecomweb.shop_service.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "main-service", url = "http://localhost:8081", configuration = FeignConfig.class)
public interface MainFeignClient {
    @PutMapping("/shop/")
    ApiResponse<?> upgradeToSeller(@RequestBody UpgradeSellerRequest request);

    @GetMapping("/users/byUsername/{username}")
    ApiResponse<?> getUserId(@PathVariable String username);
}
