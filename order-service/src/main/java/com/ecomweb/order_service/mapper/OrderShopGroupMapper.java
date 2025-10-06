package com.ecomweb.order_service.mapper;


import com.ecomweb.order_service.dto.request.OrderShopGroupRequest;
import com.ecomweb.order_service.entity.OrderShopGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderShopGroupMapper {
    @Mapping(target = "orderItems",ignore = true)
    OrderShopGroup toOrderShopGroup(OrderShopGroupRequest orderShopGroupRequest);

}
