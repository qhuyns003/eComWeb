package com.ecomweb.shop_service.mapper;


import com.ecomweb.shop_service.dto.request.ShopCreateRequest;
import com.ecomweb.shop_service.dto.request.ShopUpdateRequest;
import com.ecomweb.shop_service.dto.response.ShopResponse;
import com.ecomweb.shop_service.entity.Shop;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ShopMapper {

//    @Mapping(target = "roles", ignore = true)
    ShopResponse toShopResponse(Shop shop);

    Shop toShop(ShopCreateRequest shopCreateRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toShop(@MappingTarget Shop shop, ShopUpdateRequest shopUpdateRequest);
}
