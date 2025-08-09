package com.qhuyns.ecomweb.mapper;

import com.qhuyns.ecomweb.dto.request.ShopCreateRequest;
import com.qhuyns.ecomweb.dto.request.UserAddressRequest;
import com.qhuyns.ecomweb.dto.response.ShopAddressResponse;
import com.qhuyns.ecomweb.dto.response.UserAddressResponse;
import com.qhuyns.ecomweb.entity.ShopAddress;
import com.qhuyns.ecomweb.entity.UserAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShopAddressMapper {

    ShopAddressResponse toShopAddressResponse(ShopAddress shopAddress);
    ShopAddress toShopAddress(ShopCreateRequest shopCreateRequest);

}
