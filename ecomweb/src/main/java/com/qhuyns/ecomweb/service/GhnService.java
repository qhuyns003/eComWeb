package com.qhuyns.ecomweb.service;

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

import java.util.List;
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

}