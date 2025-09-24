package com.qhuyns.ecomweb.feignClient;

import com.qhuyns.ecomweb.configuration.FeignConfig;
import com.qhuyns.ecomweb.dto.request.ApiResponse;
import com.qhuyns.ecomweb.dto.response.ShopResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "shop-service", url = "http://localhost:8083/shop", configuration = FeignConfig.class)
public interface ShopFeignClient {

    @GetMapping("/{shopId}")
    ApiResponse<ShopResponse> getShopInfoById(@PathVariable String shopId);

    @GetMapping("/getShopId/{userId}")
    ApiResponse<String> getShopIdByUserId(@PathVariable String userId);

    @GetMapping("/getUserId/{shopId}")
    ApiResponse<String> getUserIdByShopId(@PathVariable String shopId);

    @GetMapping("/byProvinceId/{provinceId}")
    ApiResponse<List<String>> getShopIdByProvinceId(@PathVariable String provinceId);

}
