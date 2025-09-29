package com.ecomweb.message_service.mapper;

import com.ecomweb.message_service.dto.response.RoomMemberResponse;
import com.ecomweb.message_service.entity.RoomMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoomMemberMapper {
    @Mapping(target = "key",ignore = true)
    RoomMemberResponse toRoomMemberResponse(RoomMember roomMember);


}
