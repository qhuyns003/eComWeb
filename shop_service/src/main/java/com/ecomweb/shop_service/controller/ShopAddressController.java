package com.ecomweb.shop_service.controller;


import com.ecomweb.shop_service.dto.response.ApiResponse;
import com.ecomweb.shop_service.dto.response.ShopAddressResponse;
import com.ecomweb.shop_service.service.ShopAddressService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/shop_address")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ShopAddressController {

    ShopAddressService shopAddressService;

    @GetMapping("/")
    ApiResponse<List<ShopAddressResponse>> getAll(@RequestParam List<String> ids) {
        return ApiResponse.<List<ShopAddressResponse>>builder()
                .result(shopAddressService.getAll(ids))
                .build();
    }



}
