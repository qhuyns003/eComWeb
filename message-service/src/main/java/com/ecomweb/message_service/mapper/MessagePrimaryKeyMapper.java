package com.ecomweb.message_service.mapper;

import com.ecomweb.message_service.dto.response.MessagePrimaryKeyResponse;
import com.ecomweb.message_service.entity.key.MessagePrimaryKey;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessagePrimaryKeyMapper {

    MessagePrimaryKeyResponse toMessagePrimaryKeyResponse(MessagePrimaryKey messagePrimaryKey);

}
