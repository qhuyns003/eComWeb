//package com.qhuyns.ecomweb.mapper;
//
//
//import com.qhuyns.ecomweb.dto.request.UserCreationRequest;
//import com.qhuyns.ecomweb.dto.request.UserUpdateRequest;
//import com.qhuyns.ecomweb.dto.response.UserResponse;
//import com.qhuyns.ecomweb.entity.User;
//import org.mapstruct.*;
//
//@Mapper(componentModel = "spring")
//public interface UserMapper {
//    User toUser(UserCreationRequest request);
//
//    UserResponse toUserResponse(User user);
//
//    @Mapping(target = "password", ignore = true)
//    @Mapping(target = "roles", ignore = true)
//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    void updateUser(@MappingTarget User user, UserUpdateRequest request);
//}
