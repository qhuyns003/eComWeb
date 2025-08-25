package com.ecomweb.notification_service.mapper;

import com.ecomweb.notification_service.dto.request.NotificationRequest;
import com.ecomweb.notification_service.dto.response.NotificationResponse;
import com.ecomweb.notification_service.entity.Notification;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface NotificationMapper {


    @Mapping(target = "key",ignore = true)
    NotificationResponse toNotificationResponse(Notification notification);

    @Mapping(target = "key",ignore = true)
    Notification toNotification(NotificationRequest notificationRequest);


}
