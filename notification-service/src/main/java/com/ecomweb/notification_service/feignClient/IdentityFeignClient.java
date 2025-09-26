package com.ecomweb.notification_service.feignClient;


import com.ecomweb.notification_service.configuration.FeignConfig;
import com.ecomweb.notification_service.dto.response.ApiResponse;
import com.ecomweb.notification_service.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "identity-service", url = "http://localhost:8084/identity", configuration = FeignConfig.class)
public interface IdentityFeignClient {

    @GetMapping("/users/{id}")
    ApiResponse<UserResponse> getUsernameById(@PathVariable String id);

}
