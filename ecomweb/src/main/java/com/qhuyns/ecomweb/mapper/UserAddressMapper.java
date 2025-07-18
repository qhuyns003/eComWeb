package com.qhuyns.ecomweb.mapper;

import com.qhuyns.ecomweb.dto.response.CategoryResponse;
import com.qhuyns.ecomweb.dto.response.UserAddressResponse;
import com.qhuyns.ecomweb.entity.Category;
import com.qhuyns.ecomweb.entity.UserAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserAddressMapper {

    UserAddressResponse toUserAddressResponse(UserAddress userAddress);

}
