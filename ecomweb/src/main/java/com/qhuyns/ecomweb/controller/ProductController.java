package com.qhuyns.ecomweb.controller;


import com.qhuyns.ecomweb.dto.request.ApiResponse;
import com.qhuyns.ecomweb.dto.request.ProductFilterRequest;
import com.qhuyns.ecomweb.dto.request.ProductRequest;
import com.qhuyns.ecomweb.dto.response.*;
import com.qhuyns.ecomweb.exception.AppException;
import com.qhuyns.ecomweb.exception.ErrorCode;
import com.qhuyns.ecomweb.repository.UserRepository;
import com.qhuyns.ecomweb.service.CategoryService;
import com.qhuyns.ecomweb.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductController {


    UserRepository userRepository;
    ProductService productService;

    @GetMapping("/top-selling")
    ApiResponse<List<ProductOverviewResponse>> findTopSellingProducts(  @RequestParam("limit") int limit) {
        return ApiResponse.<List<ProductOverviewResponse>>builder()
                .result(productService.findTopSellingProducts(limit))
                .build();
    }

    @GetMapping("/newest")
    ApiResponse<List<ProductOverviewResponse>> findNewestProducts(  @RequestParam("limit") int limit) {
        return ApiResponse.<List<ProductOverviewResponse>>builder()
                .result(productService.findNewestProducts(limit))
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<ProductDetailResponse> getDetailProduct(@PathVariable("id") String id) {
        return ApiResponse.<ProductDetailResponse>builder()
                .result(productService.getDetailProduct(id))
                .build();
    }



    @GetMapping("/shop_list_product/{id}")
    ApiResponse<Page<ProductResponse>> findProductsWithMainImageByUserId(
            @PathVariable("id") String id,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam(value = "search", required = false,defaultValue = "") String search,
            @RequestParam(value = "status",required = false,defaultValue = "1") int status) {
        if (!userRepository.existsById(id)){
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        return ApiResponse.<Page<ProductResponse>>builder()
                .result(productService.
                        findProductsWithMainImageByUserId(id,page,size,search,status))
                .build();
    }

    @GetMapping("/searching")
    ApiResponse<Page<ProductResponse>> searchProduct(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam(value = "search", required = false,defaultValue = "") String search,
            @RequestParam(value = "status",required = false,defaultValue = "1") int status,
            @ModelAttribute ProductFilterRequest productFilterRequest) {

        return ApiResponse.<Page<ProductResponse>>builder()
                .result(productService.searchProduct(page,size,search,status,productFilterRequest))
                .build();
    }


    @GetMapping("/editing_product/{id}")
    ApiResponse<ProductResponse> getProductForUpdate(@PathVariable("id") String id){
        return ApiResponse.<ProductResponse>builder()
                .result(productService.getProductForUpdate(id))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<String> update(@RequestBody ProductRequest productRequest,
                                        @PathVariable("id") String prodId) throws IOException {
        productService.update(prodId,productRequest);
        return new ApiResponse<>().<String>builder()
                .result("Success")
                .build();
    }

    @PostMapping("")
    ApiResponse<String> create(@RequestBody ProductRequest productRequest) throws IOException {
        productService.create(productRequest);
        return new ApiResponse<>().<String>builder()
                .result("Success")
                .build();
    }

    @DeleteMapping("")
    ApiResponse<String> delete(@RequestBody List<String> ids){
        productService.delete(ids);
        return new ApiResponse<>().<String>builder()
                .result("Success")
                .build();
    }

    @GetMapping("/shop/{id}")
    ApiResponse<List<ProductOverviewResponse>> getByShopId(@PathVariable("id") String id){
        return ApiResponse.<List<ProductOverviewResponse>>builder()
                .result(productService.findByShopId(8,id))
                .build();
    }



}
