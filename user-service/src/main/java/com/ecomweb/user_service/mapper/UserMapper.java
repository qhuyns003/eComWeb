package com.ecomweb.user_service.mapper;


import com.ecomweb.user_service.dto.response.UserResponse;
import com.ecomweb.user_service.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
//    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

//    @Mapping(target = "password", ignore = true)
//    @Mapping(target = "roles", ignore = true)
//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
