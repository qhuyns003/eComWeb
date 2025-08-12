package com.qhuyns.ecomweb.repository;

import com.qhuyns.ecomweb.entity.Category;
import com.qhuyns.ecomweb.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, String> {
}
