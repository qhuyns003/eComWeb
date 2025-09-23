package com.ecomweb.shop_service.service;


import com.ecomweb.shop_service.dto.response.ShopAddressResponse;
import com.ecomweb.shop_service.mapper.ShopAddressMapper;
import com.ecomweb.shop_service.repository.ShopRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShopAddressService {

    ShopRepository shopRepository;
    ShopAddressMapper shopAddressMapper;

    public List<ShopAddressResponse> getAll(List<String> ids) {
        return shopRepository.findByShopIdIn(ids).stream()
                .map(s -> shopAddressMapper.toShopAddressResponse(s.getShopAddress()))
                .collect(Collectors.toList());
    }


}