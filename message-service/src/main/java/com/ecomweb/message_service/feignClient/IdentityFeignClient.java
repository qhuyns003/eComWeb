package com.ecomweb.message_service.feignClient;

import com.ecomweb.message_service.configuration.FeignConfig;
import com.ecomweb.message_service.dto.response.ApiResponse;
import com.ecomweb.message_service.dto.response.IntrospectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "identity-service", url = "http://localhost:8084/identity", configuration = FeignConfig.class)
public interface IdentityFeignClient {

    @PostMapping("/auth/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request);


}
