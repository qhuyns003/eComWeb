package com.ecomweb.message_service.mapper;

import com.ecomweb.message_service.dto.response.UserRoomResponse;
import com.ecomweb.message_service.entity.UserRoom;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserRoomMapper {
    @Mapping(target = "key",ignore = true)
    UserRoomResponse toUserRoomResponse(UserRoom userRoom);



}
