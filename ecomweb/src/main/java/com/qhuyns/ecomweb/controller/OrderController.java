//package com.qhuyns.ecomweb.controller;
//
//
//import com.qhuyns.ecomweb.dto.request.ApiResponse;
//import com.qhuyns.ecomweb.dto.request.OrderRequest;
//import com.qhuyns.ecomweb.dto.request.RoleRequest;
//import com.qhuyns.ecomweb.dto.response.OrderResponse;
//import com.qhuyns.ecomweb.dto.response.RoleResponse;
//import com.qhuyns.ecomweb.service.OrderService;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/orders")
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@Slf4j
//public class OrderController {
//
//    OrderService orderService;
//    @PostMapping("")
//    ApiResponse<OrderResponse> create(@RequestBody OrderRequest orderRequest) {
//
//    return new ApiResponse().<OrderResponse>builder()
//            .result(orderService.create(orderRequest))
//            .build();
//    };
//    @DeleteMapping("/{id}")
//    ApiResponse<String> delete(@PathVariable String id) {
//        orderService.delete(id);
//        return new ApiResponse().<String>builder()
//                .result("success")
//                .build();
//    };
//
//}
