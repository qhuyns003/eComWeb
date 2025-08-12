package com.qhuyns.ecomweb.repository;

import com.qhuyns.ecomweb.entity.NotificationRecipient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRecipientRepository extends JpaRepository<NotificationRecipient, String> {
    List<NotificationRecipient> findByRecipientId(String recipientId);
}

