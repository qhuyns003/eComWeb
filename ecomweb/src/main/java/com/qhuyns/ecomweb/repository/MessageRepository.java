package com.qhuyns.ecomweb.repository;

import com.qhuyns.ecomweb.entity.Category;
import com.qhuyns.ecomweb.entity.Message;
import com.qhuyns.ecomweb.entity.MessagePrimaryKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends CassandraRepository<Message, MessagePrimaryKey> {
    List<Message> findByKeyRoomIdOrderByKeySentAtAsc(String roomId);
}
