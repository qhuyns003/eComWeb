package com.ecomweb.order_service.controller;

import com.ecomweb.order_service.dto.request.OrderRequest;
import com.ecomweb.order_service.dto.response.ApiResponse;
import com.ecomweb.order_service.dto.response.OrderResponse;
import com.ecomweb.order_service.repository.OrderItemRepository;
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

    OrderItemRepository orderItemRepository;

    @GetMapping("/")
    ApiResponse<Boolean> existsOrderByProductId(@RequestParam List<String> variantIds) {
        return new ApiResponse().<Boolean>builder()
                .result(orderItemRepository.existsOrderForProduct(variantIds))
                .build();
    };


}
