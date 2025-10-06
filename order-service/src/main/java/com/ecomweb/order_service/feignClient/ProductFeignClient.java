package com.ecomweb.order_service.feignClient;

import com.ecomweb.order_service.configuration.FeignConfig;
import com.ecomweb.order_service.dto.request.IntrospectRequest;
import com.ecomweb.order_service.dto.request.OrderRequest;
import com.ecomweb.order_service.dto.request.ProductVariantStockUpdateRequest;
import com.ecomweb.order_service.dto.response.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "product-service", url = "http://localhost:8081", configuration = FeignConfig.class)
public interface ProductFeignClient {

    @PutMapping("/productVariants/stockUpdating")
    void updateStock(@RequestBody List<ProductVariantStockUpdateRequest> productVariantStockUpdateRequests);


}
