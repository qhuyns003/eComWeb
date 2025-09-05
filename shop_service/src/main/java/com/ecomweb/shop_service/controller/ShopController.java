package com.ecomweb.shop_service.controller;



import com.ecomweb.shop_service.dto.request.ShopCreateRequest;
import com.ecomweb.shop_service.dto.request.ShopUpdateRequest;
import com.ecomweb.shop_service.dto.response.ApiResponse;
import com.ecomweb.shop_service.dto.response.ShopResponse;
import com.ecomweb.shop_service.service.ShopService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ShopController {

    ShopService shopService;

    @PostMapping("")
    ApiResponse<?> create(@RequestBody ShopCreateRequest shopCreateRequest) {
        shopService.create(shopCreateRequest);
        return ApiResponse.<String>builder()
                .result("success")
                .build();
    }

    @PutMapping("")
    ApiResponse<?> update(@RequestBody ShopUpdateRequest shopUpdateRequest) {
        shopService.update(shopUpdateRequest);
        return ApiResponse.<String>builder()
                .result("success")
                .build();
    }

    @GetMapping("")
    ApiResponse<ShopResponse> getInfo() {
        return ApiResponse.<ShopResponse>builder()
                .result(shopService.getInfo())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<ShopResponse> getInfoById(@PathVariable String id) {
        return ApiResponse.<ShopResponse>builder()
                .result(shopService.getInfoById(id))
                .build();
    }








}
