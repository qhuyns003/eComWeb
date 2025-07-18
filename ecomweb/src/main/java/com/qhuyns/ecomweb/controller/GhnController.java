package com.qhuyns.ecomweb.controller;


import com.qhuyns.ecomweb.dto.request.ApiResponse;
import com.qhuyns.ecomweb.dto.request.ShippingMethodRequest;
import com.qhuyns.ecomweb.dto.response.ShippingMethodResponse;
import com.qhuyns.ecomweb.dto.response.UserAddressResponse;
import com.qhuyns.ecomweb.service.GhnService;
import com.qhuyns.ecomweb.service.UserAddressService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/methods")
    public List<ShippingMethodResponse> getShippingMethods(@RequestBody ShippingMethodRequest request) {
        return ghnService.getAvailableServices(request.getFromDistrictId(), request.getToDistrictId());
    }


}
