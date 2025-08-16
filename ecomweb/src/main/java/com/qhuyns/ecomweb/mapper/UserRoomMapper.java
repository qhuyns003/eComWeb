package com.qhuyns.ecomweb.mapper;

import com.qhuyns.ecomweb.dto.response.MessageResponse;
import com.qhuyns.ecomweb.dto.response.UserRoomResponse;
import com.qhuyns.ecomweb.entity.Message;
import com.qhuyns.ecomweb.entity.UserRoom;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserRoomMapper {
    @Mapping(target = "key",ignore = true)
    UserRoomResponse toUserRoomResponse(UserRoom  userRoom);



}
