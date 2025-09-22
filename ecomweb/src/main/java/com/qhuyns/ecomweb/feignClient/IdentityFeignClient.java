package com.qhuyns.ecomweb.feignClient;

import com.qhuyns.ecomweb.configuration.FeignConfig;
import com.qhuyns.ecomweb.dto.request.ApiResponse;
import com.qhuyns.ecomweb.dto.request.IntrospectRequest;
import com.qhuyns.ecomweb.dto.response.IntrospectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "identity-service", url = "http://localhost:8084/identity", configuration = FeignConfig.class)
public interface IdentityFeignClient {

    @PostMapping("/auth/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request);

}
