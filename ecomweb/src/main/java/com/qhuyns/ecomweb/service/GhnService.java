package com.qhuyns.ecomweb.service;

import com.qhuyns.ecomweb.dto.response.ShippingMethodResponse;
import com.qhuyns.ecomweb.dto.response.UserAddressResponse;
import com.qhuyns.ecomweb.mapper.UserAddressMapper;
import com.qhuyns.ecomweb.repository.UserAddressRepository;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public List<ShippingMethodResponse> getAvailableServices(Integer fromDistrictId,Integer toDistrictId) {
        String url = "https://online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/available-services";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", ghnToken);
        headers.set("ShopId", String.valueOf(ghnShopId));
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("shop_id", ghnShopId);
        body.put("from_district", fromDistrictId);
        body.put("to_district", toDistrictId);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        List<Map<String, Object>> data = (List<Map<String, Object>>) ((Map) response.getBody()).get("data");
        List<ShippingMethodResponse> result = new ArrayList<>();
        for (Map<String, Object> item : data) {
            ShippingMethodResponse method = new ShippingMethodResponse();
            method.setService_id((Integer) item.get("service_id"));
            method.setShort_name((String) item.get("short_name"));
            method.setService_type_id((Integer) item.get("service_type_id"));
            method.setService_name((String) item.get("service_name"));
            result.add(method);
        }
        return result;
    }

}