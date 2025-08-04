package com.qhuyns.ecomweb.service;


import com.qhuyns.ecomweb.configuration.VnpayConfig;
import com.qhuyns.ecomweb.dto.request.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VnpayService {
    @NonFinal
    @Value("${vnpay.hashSecret}")
    String hashSecret;
    VnpayConfig vnpayConfig;

    public String createPaymentUrl(String orderId, BigDecimal amount, String orderInfo, String clientIp) {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TmnCode = vnpayConfig.getTmnCode();
        String vnp_Amount = String.valueOf(amount.multiply(new BigDecimal("100")).longValue());
        String vnp_CurrCode = "VND";
        String vnp_TxnRef = orderId;
        String vnp_OrderInfo = orderInfo;
        String vnp_OrderType = "other"; // hoặc billpayment
        String vnp_Locale = "vn";
        String vnp_ReturnUrl = vnpayConfig.getReturnUrl();
        String vnp_IpAddr = clientIp;
        String vnp_CreateDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", vnp_Amount);
        vnp_Params.put("vnp_CurrCode", vnp_CurrCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", vnp_OrderType);
        vnp_Params.put("vnp_Locale", vnp_Locale);
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        // Sắp xếp tham số theo thứ tự alphabet
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String fieldValue = vnp_Params.get(fieldName);
            hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
            query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII)).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
            if (i < fieldNames.size() - 1) {
                hashData.append('&');
                query.append('&');
            }
        }
        String vnp_SecureHash = hmacSHA512(vnpayConfig.getHashSecret(), hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

        return vnpayConfig.getPaymentUrl() + "?" + query.toString();
    }


    public ApiResponse<String> vnpayIpn(Map<String, String> params) {
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
            Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA512");
            sha512_HMAC.init(secret_key);
            byte[] hash = sha512_HMAC.doFinal(data.getBytes());
            return Hex.encodeHexString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error creating HMAC SHA512", e);
        }
    }
}
