package com.qhuyns.ecomweb.controller;


import com.qhuyns.ecomweb.dto.request.ApiResponse;
import com.qhuyns.ecomweb.dto.request.UserAddressRequest;
import com.qhuyns.ecomweb.dto.response.ShopAddressResponse;
import com.qhuyns.ecomweb.dto.response.UserAddressResponse;
import com.qhuyns.ecomweb.service.ShopAddressService;
import com.qhuyns.ecomweb.service.UserAddressService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
