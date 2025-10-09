package com.ecomweb.order_service.controller;

import com.ecomweb.order_service.dto.request.OrderRequest;
import com.ecomweb.order_service.dto.response.ApiResponse;
import com.ecomweb.order_service.dto.response.OrderResponse;
import com.ecomweb.order_service.dto.response.ProductStatResponse;
import com.ecomweb.order_service.entity.OrderStatus;
import com.ecomweb.order_service.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderController {

    OrderService orderService;
    @PostMapping("/")
    ApiResponse<OrderResponse> create(@RequestBody OrderRequest orderRequest) {

    return new ApiResponse().<OrderResponse>builder()
            .result(orderService.create(orderRequest))
            .build();
    };
    @DeleteMapping("/{id}")
    ApiResponse<String> delete(@PathVariable String id) {
        orderService.delete(id);
        return new ApiResponse().<String>builder()
                .result("success")
                .build();
    };

    // khong nen get + rqbody -> co the gay loi
//    @GetMapping("/numberAndRating")
//    ApiResponse<ProductStatResponse> findNumberOfOrderAndRating(@RequestParam List<String> ids) {
//        return new ApiResponse().<ProductStatResponse>builder()
//                .result(orderService.findNumberOfOrderAndRating(OrderStatus.PAID,ids))
//                .build();
//    };

}
