package com.qhuyns.ecomweb.mapper;

import com.qhuyns.ecomweb.dto.response.MessageResponse;
import com.qhuyns.ecomweb.dto.response.UserRoomKeyResponse;
import com.qhuyns.ecomweb.entity.Message;
import com.qhuyns.ecomweb.entity.key.UserRoomKey;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserRoomKeyMapper {
    UserRoomKeyResponse toUserRoomKeyResponse(UserRoomKey userRoomKey);


}
