package com.qhuyns.ecomweb.service;

import com.qhuyns.ecomweb.dto.request.ShippingFeeRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GhnService {

    @NonFinal
    @Value("${ghn.api-url}")
    String ghnApiUrl;

    @NonFinal
    @Value("${ghn.token}")
    String ghnToken;

    @NonFinal
    @Value("${ghn.shop-id}")
    String ghnShopId;
    public String getProvinces() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", ghnToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                ghnApiUrl + "master-data/province",
                HttpMethod.GET,
                entity,
                String.class
        );
        return response.getBody();
    }
    public String getDistricts(int provinceId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", ghnToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = ghnApiUrl + "master-data/district?province_id=" + provinceId;

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );
        return response.getBody();
    }

    public String getWards(int districtId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", ghnToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = ghnApiUrl + "master-data/ward?district_id=" + districtId;

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );
        return response.getBody();
    }

    public Map<String, Object> getAvailableService(Integer fromDistrictId, Integer toDistrictId) {
        String url = "https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/available-services";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", ghnToken);
        headers.set("ShopId", ghnShopId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("shop_id", Integer.valueOf(ghnShopId));
        body.put("from_district", fromDistrictId);
        body.put("to_district", toDistrictId);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        // Trả về toàn bộ data hoặc chỉ serviceId mong muốn
        return (Map<String, Object>) response.getBody();
    }


    public Map<String, Object> calculateFee(ShippingFeeRequest request) {
        String url = "https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", ghnToken);
        headers.set("ShopId", ghnShopId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = new HashMap<>();
        payload.put("service_type_id", request.getServiceTypeId());
        payload.put("from_district_id", request.getFromDistrictId());
        payload.put("from_ward_code", request.getFromWardCode());
        payload.put("to_district_id", request.getToDistrictId());
        payload.put("to_ward_code", request.getToWardCode());
        payload.put("weight", request.getWeight());
        payload.put("length", request.getLength());
        payload.put("width", request.getWidth());
        payload.put("height", request.getHeight());
        payload.put("insurance_value", request.getInsuranceValue() != null ? request.getInsuranceValue() : 0);
        payload.put("coupon", request.getCoupon() != null ? request.getCoupon() : "");
        if (request.getServiceTypeId() == 5) {
            payload.put("items", request.getItems());
        }

        if (request.getServiceTypeId() != null && request.getServiceTypeId() == 5 && request.getItems() != null) {
            // Hàng nặng
            payload.put("items", request.getItems());
        } else {
            // Hàng nhẹ
            payload.put("weight", request.getWeight());
            payload.put("length", request.getLength());
            payload.put("width", request.getWidth());
            payload.put("height", request.getHeight());
        }

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        return response.getBody();
    }


}