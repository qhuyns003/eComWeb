//package com.qhuyns.ecomweb.mapper;
//
//import com.qhuyns.ecomweb.dto.request.ShopCreateRequest;
//import com.qhuyns.ecomweb.dto.request.ShopUpdateRequest;
//import com.qhuyns.ecomweb.dto.request.UserAddressRequest;
//import com.qhuyns.ecomweb.dto.response.ShopAddressResponse;
//import com.qhuyns.ecomweb.dto.response.UserAddressResponse;
//import com.qhuyns.ecomweb.entity.Shop;
//import com.qhuyns.ecomweb.entity.ShopAddress;
//import com.qhuyns.ecomweb.entity.UserAddress;
//import org.mapstruct.*;
//
//@Mapper(componentModel = "spring")
//public interface ShopAddressMapper {
//
//    ShopAddressResponse toShopAddressResponse(ShopAddress shopAddress);
//    ShopAddress toShopAddress(ShopCreateRequest shopCreateRequest);
//
//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    void toShopAddress(@MappingTarget ShopAddress shopAddress, ShopUpdateRequest shopUpdateRequest);
//}
