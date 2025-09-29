package com.ecomweb.message_service.repository;

import com.ecomweb.message_service.entity.Message;
import com.ecomweb.message_service.entity.key.MessagePrimaryKey;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

public interface MessageRepository extends CassandraRepository<Message, MessagePrimaryKey> {
    List<Message> findByKeyRoomIdOrderByKeySentAtAsc(String roomId);
}
