package com.qhuyns.ecomweb.mapper;

import com.qhuyns.ecomweb.dto.response.RoomMemberKeyResponse;
import com.qhuyns.ecomweb.dto.response.RoomMemberResponse;
import com.qhuyns.ecomweb.entity.RoomMember;
import com.qhuyns.ecomweb.entity.key.RoomMemberKey;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoomMemberKeyMapper {
    RoomMemberKeyResponse toRoomMemberKeyResponse(RoomMemberKey roomMemberKey);


}
