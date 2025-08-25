package com.ecomweb.notification_service.entity;

//import jakarta.persistence.*;
import com.ecomweb.notification_service.entity.key.NotificationKey;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table("notifications")
public class Notification {
    @PrimaryKey
    private NotificationKey key;

    private String type;
    private String title;
    private String message;
    private String status;


}