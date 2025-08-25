package com.ecomweb.notification_service.mapper;

import com.ecomweb.notification_service.dto.request.NotificationKeyRequest;
import com.ecomweb.notification_service.dto.response.NotificationKeyResponse;
import com.ecomweb.notification_service.dto.response.NotificationResponse;
import com.ecomweb.notification_service.entity.Notification;
import com.ecomweb.notification_service.entity.key.NotificationKey;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationKeyMapper {


    NotificationKeyResponse toNotificationKeyResponse(NotificationKey notificationKey);


    NotificationKey toNotificationKey(NotificationKeyRequest notificationKeyRequest);

}
