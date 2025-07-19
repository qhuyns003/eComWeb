package com.qhuyns.ecomweb.controller;


import com.qhuyns.ecomweb.dto.request.ApiResponse;
import com.qhuyns.ecomweb.dto.request.GhnAvailableServiceRequest;
import com.qhuyns.ecomweb.service.GhnService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ghn")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class GhnController {
    GhnService ghnService;

    @GetMapping("/provinces")
    public String getProvinces() {
        return ghnService.getProvinces();
    }

    @GetMapping("/districts")
    public String getDistricts(@RequestParam int provinceId) {
        return ghnService.getDistricts(provinceId);
    }

    @GetMapping("/wards")
    public String getWards(@RequestParam int districtId) {
        return ghnService.getWards(districtId);
    }


    @PostMapping("/available-service")
    public ApiResponse<?> getGhnServiceForOrderGroup(@RequestBody List<GhnAvailableServiceRequest> orderGroups) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (GhnAvailableServiceRequest group : orderGroups) {
            Map<String, Object> serviceInfo = ghnService.getAvailableService(group.getFromDistrictId(), group.getToDistrictId());
            serviceInfo.put("shopId", group.getShopId());
            result.add(serviceInfo);
        }
        return ApiResponse.builder()
                .result(result)
                .build();
    }

    


}
