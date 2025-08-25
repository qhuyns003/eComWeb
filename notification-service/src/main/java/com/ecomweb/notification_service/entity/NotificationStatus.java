package com.ecomweb.notification_service.entity;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum NotificationStatus {
    READ,
    UNREAD;


}
