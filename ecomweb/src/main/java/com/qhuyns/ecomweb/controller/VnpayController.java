package com.qhuyns.ecomweb.controller;

import com.qhuyns.ecomweb.dto.request.ApiResponse;
import com.qhuyns.ecomweb.dto.request.VnpayPaymentRequest;
import com.qhuyns.ecomweb.service.VnpayService;
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

    @NonFinal
    @Value("${vnpay.hashSecret}")
    String hashSecret;

    @PostMapping("/create-payment")
    public ApiResponse<?> createPayment(@RequestBody VnpayPaymentRequest req, HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        if ("0:0:0:0:0:0:0:1".equals(clientIp)) clientIp = "127.0.0.1";
        String paymentUrl = vnpayService.createPaymentUrl(req.getOrderId(), req.getAmount(), req.getOrderInfo(), clientIp);
        return ApiResponse.builder()
                .result(Map.of("paymentUrl", paymentUrl))
                .build();
    }


    @PostMapping("/ipn")
    public ApiResponse<String> vnpayIpn(@RequestParam Map<String, String> params) {
        String vnp_SecureHash = params.get("vnp_SecureHash");
        params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String fieldValue = params.get(fieldName);
            hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
            if (i < fieldNames.size() - 1) hashData.append('&');
        }
        String vnp_HashSecret = hashSecret;
        String checkSum = hmacSHA512(vnp_HashSecret, hashData.toString());
        String orderId = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");
        if (checkSum.equalsIgnoreCase(vnp_SecureHash)) {
            if ("00".equals(responseCode)) {
                // Cập nhật trạng thái đơn hàng thành PAID ở đây
                return ApiResponse.<String>builder()
                        .code(1000)
                        .message("IPN: Thanh toán thành công cho đơn hàng: " + orderId)
                        .result("00")
                        .build();
            } else {
                // Cập nhật trạng thái đơn hàng thành FAILED ở đây
                return ApiResponse.<String>builder()
                        .code(1002)
                        .message("IPN: Thanh toán thất bại, mã lỗi: " + responseCode)
                        .result("00")
                        .build();
            }
        } else {
            return ApiResponse.<String>builder()
                    .code(400)
                    .message("Sai checksum!")
                    .result("97")
                    .build();
        }
    }

    private String hmacSHA512(String key, String data) {
        try {
            javax.crypto.Mac sha512_HMAC = javax.crypto.Mac.getInstance("HmacSHA512");
            javax.crypto.spec.SecretKeySpec secret_key = new javax.crypto.spec.SecretKeySpec(key.getBytes(), "HmacSHA512");
            sha512_HMAC.init(secret_key);
            byte[] hash = sha512_HMAC.doFinal(data.getBytes());
            // Nếu đã thêm commons-codec:
            return org.apache.commons.codec.binary.Hex.encodeHexString(hash);
            // Nếu không muốn dùng thư viện ngoài, thay bằng:
            // return bytesToHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error creating HMAC SHA512", e);
        }
    }

}

