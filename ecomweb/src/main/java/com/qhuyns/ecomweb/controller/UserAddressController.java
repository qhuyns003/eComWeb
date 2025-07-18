package com.qhuyns.ecomweb.controller;


import com.qhuyns.ecomweb.dto.request.ApiResponse;
import com.qhuyns.ecomweb.dto.response.CategoryResponse;
import com.qhuyns.ecomweb.dto.response.UserAddressResponse;
import com.qhuyns.ecomweb.service.CategoryService;
import com.qhuyns.ecomweb.service.UserAddressService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user_address")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserAddressController {

    UserAddressService userAddressService;

    @GetMapping("/")
    ApiResponse<List<UserAddressResponse>> getAll() {
        return ApiResponse.<List<UserAddressResponse>>builder()
                .result(userAddressService.getAll())
                .build();
    }
}
