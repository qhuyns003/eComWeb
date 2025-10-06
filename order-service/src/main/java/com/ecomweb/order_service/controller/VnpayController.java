package com.ecomweb.order_service.controller;

import com.ecomweb.order_service.dto.request.VnpayPaymentRequest;
import com.ecomweb.order_service.dto.response.ApiResponse;
import com.ecomweb.order_service.service.VnpayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/vnpay")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VnpayController {
    VnpayService vnpayService;


    @PostMapping("/create-payment")
    public ApiResponse<?> createPayment(@RequestBody VnpayPaymentRequest req, HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        if ("0:0:0:0:0:0:0:1".equals(clientIp)) clientIp = "127.0.0.1";
        String paymentUrl = vnpayService.createPaymentUrl(req.getOrderId(), req.getAmount(), req.getOrderInfo(), clientIp);
        return ApiResponse.builder()
                .result(Map.of("paymentUrl", paymentUrl))
                .build();
    }

    @GetMapping("/payment-status")
    public ApiResponse<?> getPaymentStatus(@RequestParam("vnp_TxnRef") String orderId) {
        // kiem tra don hang PAID chua
        return ApiResponse.builder()
                .result("success")
                .build();
    }

    @RequestMapping(value = "/IPN", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResponse<String> vnpayIpn(@RequestParam Map<String, String> params) {
       return vnpayService.vnpayIpn(params);
    }



}

