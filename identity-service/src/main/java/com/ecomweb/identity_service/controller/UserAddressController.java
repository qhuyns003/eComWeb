package com.ecomweb.identity_service.controller;


import com.ecomweb.identity_service.dto.request.UserAddressRequest;
import com.ecomweb.identity_service.dto.response.ApiResponse;
import com.ecomweb.identity_service.dto.response.UserAddressResponse;
import com.ecomweb.identity_service.service.UserAddressService;
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

    @GetMapping("/{id}")
    ApiResponse<UserAddressResponse> getById(@PathVariable String id) {
        return ApiResponse.<UserAddressResponse>builder()
                .result(userAddressService.getById(id))
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
