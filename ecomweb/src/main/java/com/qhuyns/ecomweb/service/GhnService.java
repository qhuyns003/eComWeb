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


    public Map<String, Object> calculateFee(ShippingFeeRequest req) {
        String url = "https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", ghnToken);
        headers.set("ShopId", ghnShopId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("service_id", req.getServiceId());
        body.put("from_district_id", req.getFromDistrictId());
        body.put("to_district_id", req.getToDistrictId());
        body.put("to_ward_code", req.getToWardCode());
        body.put("insurance_value", req.getInsurance_value());
        body.put("coupon", req.getCoupon());

        if (req.getServiceTypeId() != null && req.getServiceTypeId() == 5 && req.getItems() != null) {
            // Hàng nặng
            body.put("items", req.getItems());
        } else {
            // Hàng nhẹ
            body.put("weight", req.getWeight());
            body.put("length", req.getLength());
            body.put("width", req.getWidth());
            body.put("height", req.getHeight());
        }

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        return response.getBody();
    }


}