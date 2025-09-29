package com.ecomweb.message_service.feignClient;

import com.ecomweb.message_service.configuration.FeignConfig;
import com.ecomweb.message_service.dto.request.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "shop-service", url = "http://localhost:8083/shop", configuration = FeignConfig.class)
public interface ShopFeignClient {

//    @GetMapping("/{shopId}")
//    ApiResponse<ShopResponse> getShopInfoById(@PathVariable("shopId") String shopId);

    @GetMapping("/getShopId/{userId}")
    ApiResponse<String> getShopIdByUserId(@PathVariable("userId") String userId);

    @GetMapping("/getUserId/{shopId}")
    ApiResponse<String> getUserIdByShopId(@PathVariable("shopId") String shopId);

    @GetMapping("/byProvinceId/{provinceId}")
    ApiResponse<List<String>> getShopIdByProvinceId(@PathVariable("provinceId") String provinceId);

}
