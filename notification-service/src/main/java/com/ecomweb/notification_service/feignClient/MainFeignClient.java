package com.ecomweb.notification_service.feignClient;


import com.ecomweb.notification_service.configuration.FeignConfig;
import com.ecomweb.notification_service.dto.response.ApiResponse;
import com.ecomweb.notification_service.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "main-service", url = "http://localhost:8081", configuration = FeignConfig.class)
public interface MainFeignClient {

    @GetMapping("/users/{id}")
    ApiResponse<String> getUsernameById(@PathVariable String id);

}
