package com.ecomweb.order_service.controller;

import com.ecomweb.order_service.dto.request.OrderRequest;
import com.ecomweb.order_service.dto.response.ApiResponse;
import com.ecomweb.order_service.dto.response.OrderResponse;
import com.ecomweb.order_service.dto.response.UserResponse;
import com.ecomweb.order_service.repository.OrderItemRepository;
import com.ecomweb.order_service.service.OrderItemService;
import com.ecomweb.order_service.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orderItems")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderItemController {

    OrderItemService orderItemService;

    @GetMapping("/")
    ApiResponse<Boolean> existsOrderByProductId(@RequestParam List<String> variantIds) {
        return new ApiResponse().<Boolean>builder()
                .result(orderItemService.existsOrderForProduct(variantIds))
                .build();
    };

    @GetMapping("/numberOfOrder/")
    ApiResponse<Long> getNumberOfOrder(@RequestParam List<String> variantIds) {
        return new ApiResponse().<Long>builder()
                .result(orderItemService.getNumberOfOrder(variantIds))
                .build();
    };

    @GetMapping("/ownerOfReview/")
    ApiResponse<UserResponse> getOwnerOfReview(@RequestParam String orderItemId) {
        return new ApiResponse().<UserResponse>builder()
                .result(orderItemService.getOwnerOfOrder(orderItemId))
                .build();
    };



}
