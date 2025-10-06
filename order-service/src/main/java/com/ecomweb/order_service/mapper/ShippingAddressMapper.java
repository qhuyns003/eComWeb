package com.ecomweb.order_service.mapper;

import com.ecomweb.order_service.dto.response.UserAddressResponse;
import com.ecomweb.order_service.entity.ShippingAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShippingAddressMapper {

    @Mapping(target = "id", ignore = true)
    ShippingAddress toShippingAddress(UserAddressResponse userAddressResponse);

}
