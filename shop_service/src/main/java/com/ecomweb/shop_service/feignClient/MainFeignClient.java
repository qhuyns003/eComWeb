package com.ecomweb.shop_service.feignClient;

import com.ecomweb.shop_service.configuration.FeignConfig;
import com.ecomweb.shop_service.dto.request.IntrospectRequest;
import com.ecomweb.shop_service.dto.request.UpgradeSellerRequest;
import com.ecomweb.shop_service.dto.response.ApiResponse;
import com.ecomweb.shop_service.dto.response.IntrospectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.crypto.spec.SecretKeySpec;

@FeignClient(name = "main-service", url = "http://localhost:8081", configuration = FeignConfig.class)
public interface MainFeignClient {
    @PutMapping("/shop/")
    ApiResponse<?> upgradeToSeller(@RequestBody UpgradeSellerRequest request);

    @GetMapping("/users/byUsername/{username}")
    ApiResponse<?> getUserId(@PathVariable String username);

    @PostMapping("/auth/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request);

}
