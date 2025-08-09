package com.qhuyns.ecomweb.mapper;


import com.qhuyns.ecomweb.dto.request.ShopCreateRequest;
import com.qhuyns.ecomweb.dto.request.ShopUpdateRequest;
import com.qhuyns.ecomweb.dto.request.UserCreationRequest;
import com.qhuyns.ecomweb.dto.request.UserUpdateRequest;
import com.qhuyns.ecomweb.dto.response.ShopResponse;
import com.qhuyns.ecomweb.dto.response.UserResponse;
import com.qhuyns.ecomweb.entity.Shop;
import com.qhuyns.ecomweb.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ShopMapper {

//    @Mapping(target = "roles", ignore = true)
    ShopResponse toShopResponse(Shop shop);

    Shop toShop(ShopCreateRequest shopCreateRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toShop(@MappingTarget Shop shop, ShopUpdateRequest shopUpdateRequest);
}
