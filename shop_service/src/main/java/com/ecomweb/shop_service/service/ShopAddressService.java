package com.ecomweb.shop_service.service;


import com.ecomweb.shop_service.dto.response.ShopAddressResponse;
import com.ecomweb.shop_service.exception.AppException;
import com.ecomweb.shop_service.exception.ErrorCode;
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
// có 2 hướng giải quyết vấn đề câu querry cần truy vấn trên bảng service khác
// cách1 : embeedded thuôc tính vào bảng hiện thời
// cách2 : gọi sang service để lấy id thỏa mãn, intersection giữa các id và truyền vào querry
// cách3 : sử dụng server ES de lam server trung gian luu thực thể tổng hợp dữ liệu
public class ShopAddressService {

    ShopRepository shopRepository;
    ShopAddressMapper shopAddressMapper;

    public List<ShopAddressResponse> getAll(List<String> ids) {

        return shopRepository.findByIdIn(ids).stream()
                .map(s -> {
                    ShopAddressResponse res = shopAddressMapper.toShopAddressResponse(s.getShopAddress());
                    res.setId(s.getId());
                    return res;
                })
                .collect(Collectors.toList());
    }





}