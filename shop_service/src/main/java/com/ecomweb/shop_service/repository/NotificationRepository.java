//package com.ecomweb.shop_service.repository;
//import com.ecomweb.shop_service.entity.Notification;
//import com.ecomweb.shop_service.entity.key.NotificationKey;
//import org.springframework.data.cassandra.repository.CassandraRepository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.UUID;
//
//public interface NotificationRepository extends CassandraRepository<Notification, NotificationKey> {
//
//    // Truy vấn thông báo mới nhất
//    List<Notification> findByKeyUserIdOrderByKeyCreatedAtDesc(String userId);
//
//    Notification findByKeyUserIdAndKeyCreatedAtAndKeyNotificationId(String userId, LocalDateTime createdAt, String notificationId);
//}