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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ShopController {

    ShopService shopService;

    @PostMapping("/create")
    ResponseEntity<ApiResponse<?>> create(@RequestBody ShopCreateRequest shopCreateRequest) throws Exception {
        ApiResponse<?> apiResponse = shopService.create(shopCreateRequest);
        return ResponseEntity.status(apiResponse.getHttpStatus())
                .body(apiResponse);
    }

    @PutMapping("")
    ResponseEntity<ApiResponse<?>> update(@RequestBody ShopUpdateRequest shopUpdateRequest) {
        ApiResponse<?> apiResponse=shopService.update(shopUpdateRequest);
        return ResponseEntity
                .status(apiResponse.getHttpStatus())
                .body(apiResponse);
    }

    @GetMapping("")
    ResponseEntity<ApiResponse<?>> getInfo() {
        ApiResponse<?> apiResponse = shopService.getInfo();
        return ResponseEntity
                .status(apiResponse.getHttpStatus())
                .body(apiResponse);
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<?>> getInfoById(@PathVariable String id) {
        ApiResponse<?> apiResponse = shopService.getInfoById(id);
        return ResponseEntity
                .status(apiResponse.getHttpStatus())
                .body(apiResponse);
    }








}
