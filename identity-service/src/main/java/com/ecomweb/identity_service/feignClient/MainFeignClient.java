package com.ecomweb.identity_service.feignClient;


import com.ecomweb.identity_service.configuration.FeignConfig;
import com.ecomweb.identity_service.dto.request.IntrospectRequest;
import com.ecomweb.identity_service.dto.response.ApiResponse;
import com.ecomweb.identity_service.dto.response.IntrospectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

// gan annotation vao application moi dung dc
@FeignClient(name = "main-service", url = "http://localhost:8081", configuration = FeignConfig.class)
public interface MainFeignClient {

    @PostMapping("/auth/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request);


}
