package com.qhuyns.ecomweb.controller;


import com.qhuyns.ecomweb.dto.request.ApiResponse;
import com.qhuyns.ecomweb.dto.request.UserAddressRequest;
import com.qhuyns.ecomweb.dto.response.CategoryResponse;
import com.qhuyns.ecomweb.dto.response.UserAddressResponse;
import com.qhuyns.ecomweb.service.CategoryService;
import com.qhuyns.ecomweb.service.UserAddressService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/")
    ApiResponse<String> create(@RequestBody UserAddressRequest userAddressRequest) {
        userAddressService.create(userAddressRequest);
        return ApiResponse.<String>builder()
                .result("Successfully created user address")
                .build();
    }
    @DeleteMapping("/{id}")
    ApiResponse<?> delete(@PathVariable String id) {
        userAddressService.delete(id);
        return ApiResponse.<String>builder()
                .result("Successfully deleted user address")
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<?> update(@PathVariable String id,@RequestBody UserAddressRequest userAddressRequest) {
        userAddressService.update(id, userAddressRequest);
        return ApiResponse.<String>builder()
                .result("Successfully updated user address")
                .build();
    }


}
