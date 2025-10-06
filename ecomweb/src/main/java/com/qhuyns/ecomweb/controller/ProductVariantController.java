package com.qhuyns.ecomweb.controller;


import com.qhuyns.ecomweb.dto.request.ApiResponse;
import com.qhuyns.ecomweb.dto.request.ProductFilterRequest;
import com.qhuyns.ecomweb.dto.request.ProductRequest;
import com.qhuyns.ecomweb.dto.request.ProductVariantStockUpdateRequest;
import com.qhuyns.ecomweb.dto.response.ProductDetailResponse;
import com.qhuyns.ecomweb.dto.response.ProductOverviewResponse;
import com.qhuyns.ecomweb.dto.response.ProductResponse;
import com.qhuyns.ecomweb.service.ProductService;
import com.qhuyns.ecomweb.service.ProductVariantService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/productVariants")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductVariantController {


    ProductVariantService productVariantService;

    @PutMapping("/stockUpdating")
    ApiResponse<?> updateStock(@RequestBody ProductVariantStockUpdateRequest productVariantStockUpdateRequest){
        return ApiResponse.builder()
                .result("successful")
                .build();
    }



}
