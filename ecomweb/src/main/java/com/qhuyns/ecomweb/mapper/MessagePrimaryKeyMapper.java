package com.qhuyns.ecomweb.mapper;

import com.qhuyns.ecomweb.dto.response.MessagePrimaryKeyResponse;
import com.qhuyns.ecomweb.dto.response.MessageResponse;
import com.qhuyns.ecomweb.entity.Message;
import com.qhuyns.ecomweb.entity.key.MessagePrimaryKey;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessagePrimaryKeyMapper {

    MessagePrimaryKeyResponse toMessagePrimaryKeyResponse(MessagePrimaryKey messagePrimaryKey);

}
