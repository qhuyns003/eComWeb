package com.qhuyns.ecomweb.mapper;

import com.qhuyns.ecomweb.dto.response.CategoryResponse;
import com.qhuyns.ecomweb.dto.response.NotificationResponse;
import com.qhuyns.ecomweb.entity.Category;
import com.qhuyns.ecomweb.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationResponse toNotificationResponse(Notification notification);



}
