package com.ecomweb.identity_service.mapper;


import com.ecomweb.identity_service.dto.request.UserCreationRequest;
import com.ecomweb.identity_service.dto.request.UserUpdateRequest;
import com.ecomweb.identity_service.dto.response.UserResponse;
import com.ecomweb.identity_service.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    @Mapping(target = "password", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
