package com.qhuyns.ecomweb.feignClient;

import com.qhuyns.ecomweb.configuration.FeignConfig;
import com.qhuyns.ecomweb.dto.request.ApiResponse;
import com.qhuyns.ecomweb.dto.response.ProductStatResponse;
import com.qhuyns.ecomweb.dto.response.ShopResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "order-service", url = "http://localhost:8086/orders", configuration = FeignConfig.class)
public interface OrderFeignClient {

    @GetMapping("/numberAndRating")
    ApiResponse<ProductStatResponse> findNumberOfOrderAndRating(@RequestParam List<String> ids);

    @GetMapping("/orderItems/")
    ApiResponse<Boolean> existsOrderByProductId(@RequestParam List<String> variantIds);

    @GetMapping("/orderItems/numberOfOrder/")
    ApiResponse<Long> getNumberOfOrder(@RequestParam List<String> variantIds);

}
