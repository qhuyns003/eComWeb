package com.ecomweb.order_service.controller;


import com.ecomweb.order_service.dto.response.ApiResponse;
import com.ecomweb.order_service.dto.response.PaymentResponse;
import com.ecomweb.order_service.service.PaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PaymentController {
    PaymentService paymentService;

    @GetMapping("/")
    ApiResponse<List<PaymentResponse>> getAll() {
        return ApiResponse.<List<PaymentResponse>>builder()
                .result(paymentService.getAll())
                .build();
    }

}
