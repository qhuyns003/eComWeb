package com.qhuyns.ecomweb.mapper;


import com.qhuyns.ecomweb.dto.request.OrderRequest;
import com.qhuyns.ecomweb.dto.request.PermissionRequest;
import com.qhuyns.ecomweb.dto.response.PermissionResponse;
import com.qhuyns.ecomweb.entity.Order;
import com.qhuyns.ecomweb.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "orderShopGroups",ignore = true)
    Order toOrder(OrderRequest orderRequest);

}
