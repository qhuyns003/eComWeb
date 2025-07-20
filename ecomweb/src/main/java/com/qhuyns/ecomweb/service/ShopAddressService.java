package com.qhuyns.ecomweb.service;

import com.qhuyns.ecomweb.dto.request.UserAddressRequest;
import com.qhuyns.ecomweb.dto.response.ShopAddressResponse;
import com.qhuyns.ecomweb.dto.response.UserAddressResponse;
import com.qhuyns.ecomweb.entity.UserAddress;
import com.qhuyns.ecomweb.exception.AppException;
import com.qhuyns.ecomweb.exception.ErrorCode;
import com.qhuyns.ecomweb.mapper.ShopAddressMapper;
import com.qhuyns.ecomweb.mapper.UserAddressMapper;
import com.qhuyns.ecomweb.repository.ShopAddressRepository;
import com.qhuyns.ecomweb.repository.UserAddressRepository;
import com.qhuyns.ecomweb.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShopAddressService {

    ShopAddressRepository shopAddressRepository;
    ShopAddressMapper shopAddressMapper;

    public List<ShopAddressResponse> getAll(List<String> ids) {
        return shopAddressRepository.findByShopIds(ids).stream().map(shopAddressMapper::toShopAddressResponse).collect(Collectors.toList());
    }


}