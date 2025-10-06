package com.ecomweb.order_service.mapper;


import com.ecomweb.order_service.dto.request.OrderRequest;
import com.ecomweb.order_service.dto.response.OrderResponse;
import com.ecomweb.order_service.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "orderShopGroups",ignore = true)
    Order toOrder(OrderRequest orderRequest);

    OrderResponse toOrderResponse(Order order);

}
