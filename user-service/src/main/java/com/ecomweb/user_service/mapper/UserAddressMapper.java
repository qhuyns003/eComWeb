package com.ecomweb.user_service.mapper;

import com.ecomweb.user_service.dto.response.UserAddressResponse;
import com.ecomweb.user_service.entity.UserAddress;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserAddressMapper {

    // tranh dat chu is vi lombok sinh getter voi boolean tu dong + is + tenbien
    // con mapstruct va java bean thi luon mac dinh boolean getter luon la isDefault ke ca ten bien co la default hay isDefault
    // => viet them getter isTenBien hoac k dat ten bien co chu is
//    UserAddress toUserAddress(UserAddressRequest userAddressRequest);

    UserAddressResponse toUserAddressResponse(UserAddress userAddress);


//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    void toUserAddress(@MappingTarget UserAddress userAddress, UserAddressRequest userAddressRequest);

}
