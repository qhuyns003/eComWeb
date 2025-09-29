package com.ecomweb.message_service.mapper;

import com.ecomweb.message_service.dto.response.RoomResponse;
import com.ecomweb.message_service.entity.Room;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    RoomResponse toRoomResponse(Room room);



}
