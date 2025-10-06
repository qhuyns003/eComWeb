package com.qhuyns.ecomweb.mapper;


import com.qhuyns.ecomweb.dto.request.OrderItemRequest;
import com.qhuyns.ecomweb.dto.request.OrderRequest;
import com.qhuyns.ecomweb.entity.Order;
import com.qhuyns.ecomweb.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    OrderItem toOrderItem(OrderItemRequest orderItemRequest);

}
