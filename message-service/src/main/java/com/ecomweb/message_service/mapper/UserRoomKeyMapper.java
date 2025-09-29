package com.ecomweb.message_service.mapper;

import com.ecomweb.message_service.dto.response.UserRoomKeyResponse;
import com.ecomweb.message_service.entity.key.UserRoomKey;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserRoomKeyMapper {
    UserRoomKeyResponse toUserRoomKeyResponse(UserRoomKey userRoomKey);


}
