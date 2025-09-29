package com.ecomweb.message_service.mapper;

import com.ecomweb.message_service.dto.response.MessageResponse;
import com.ecomweb.message_service.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    @Mapping(target = "key",ignore = true)
    MessageResponse toMessageResponse(Message message);



}
