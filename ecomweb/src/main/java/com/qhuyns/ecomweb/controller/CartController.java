package com.qhuyns.ecomweb.controller;


import com.qhuyns.ecomweb.dto.request.ApiResponse;
import com.qhuyns.ecomweb.dto.request.CartRequest;
import com.qhuyns.ecomweb.dto.request.RoleRequest;
import com.qhuyns.ecomweb.dto.response.CartResponse;
import com.qhuyns.ecomweb.dto.response.RoleResponse;
import com.qhuyns.ecomweb.service.CartService;
import com.qhuyns.ecomweb.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CartController {

    CartService cartService;
    @GetMapping
    ApiResponse<List<CartResponse>> getAll() {
        return ApiResponse.<List<CartResponse>>builder()
                .result(cartService.getAll())
                .build();
    }
    @PostMapping
    ApiResponse<?> addToCart(@RequestBody CartRequest cartRequest) {
        cartService.addToCart(cartRequest);
        return ApiResponse.<String>builder()
                .result("success")
                .build();
    }


}
