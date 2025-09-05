package com.ecomweb.shop_service.mapper;


import com.ecomweb.shop_service.dto.request.ShopCreateRequest;
import com.ecomweb.shop_service.dto.request.ShopUpdateRequest;
import com.ecomweb.shop_service.dto.response.ShopAddressResponse;
import com.ecomweb.shop_service.entity.ShopAddress;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ShopAddressMapper {

    ShopAddressResponse toShopAddressResponse(ShopAddress shopAddress);
    ShopAddress toShopAddress(ShopCreateRequest shopCreateRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toShopAddress(@MappingTarget ShopAddress shopAddress, ShopUpdateRequest shopUpdateRequest);
}
