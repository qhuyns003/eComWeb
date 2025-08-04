package com.qhuyns.ecomweb.controller;


import com.qhuyns.ecomweb.dto.request.ApiResponse;
import com.qhuyns.ecomweb.dto.request.RoleRequest;
import com.qhuyns.ecomweb.dto.response.PaymentResponse;
import com.qhuyns.ecomweb.dto.response.RoleResponse;
import com.qhuyns.ecomweb.service.PaymentService;
import com.qhuyns.ecomweb.service.RoleService;
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

    @GetMapping
    ApiResponse<List<PaymentResponse>> getAll() {
        return ApiResponse.<List<PaymentResponse>>builder()
                .result(paymentService.getAll())
                .build();
    }

}
