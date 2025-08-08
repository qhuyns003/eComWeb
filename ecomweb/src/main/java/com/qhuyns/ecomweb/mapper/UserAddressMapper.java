package com.qhuyns.ecomweb.mapper;

import com.qhuyns.ecomweb.dto.request.UserAddressRequest;
import com.qhuyns.ecomweb.dto.response.CategoryResponse;
import com.qhuyns.ecomweb.dto.response.UserAddressResponse;
import com.qhuyns.ecomweb.entity.Category;
import com.qhuyns.ecomweb.entity.UserAddress;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserAddressMapper {

    // tranh dat chu is vi lombok sinh getter voi boolean tu dong + is + tenbien
    // con mapstruct va java bean thi luon mac dinh boolean getter luon la isDefault ke ca ten bien co la default hay isDefault
    // => viet them getter isTenBien hoac k dat ten bien co chu is
    UserAddress toUserAddress(UserAddressRequest userAddressRequest);

    UserAddressResponse toUserAddressResponse(UserAddress userAddress);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toUserAddress(@MappingTarget UserAddress userAddress, UserAddressRequest userAddressRequest);

}
