package com.ecomweb.order_service.mapper;


import com.ecomweb.order_service.dto.request.OrderItemRequest;
import com.ecomweb.order_service.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    OrderItem toOrderItem(OrderItemRequest orderItemRequest);

}
