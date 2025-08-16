package com.qhuyns.ecomweb.mapper;

import com.qhuyns.ecomweb.dto.response.CategoryResponse;
import com.qhuyns.ecomweb.dto.response.RoomMemberResponse;
import com.qhuyns.ecomweb.entity.Category;
import com.qhuyns.ecomweb.entity.RoomMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoomMemberMapper {
    @Mapping(target = "key",ignore = true)
    RoomMemberResponse toRoomMemberResponse(RoomMember roomMember);


}
