package com.qhuyns.ecomweb.repository;

import com.qhuyns.ecomweb.entity.Message;
import com.qhuyns.ecomweb.entity.key.MessagePrimaryKey;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

public interface MessageRepository extends CassandraRepository<Message, MessagePrimaryKey> {
    List<Message> findByKeyRoomIdOrderByKeySentAtAsc(String roomId);
}
