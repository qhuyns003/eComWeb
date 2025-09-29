package com.ecomweb.message_service.mapper;

import com.ecomweb.message_service.dto.response.RoomMemberKeyResponse;
import com.ecomweb.message_service.entity.key.RoomMemberKey;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomMemberKeyMapper {
    RoomMemberKeyResponse toRoomMemberKeyResponse(RoomMemberKey roomMemberKey);


}
