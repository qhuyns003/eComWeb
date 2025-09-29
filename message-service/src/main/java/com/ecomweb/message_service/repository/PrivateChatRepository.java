package com.ecomweb.message_service.repository;

import com.ecomweb.message_service.entity.PrivateChat;
import com.ecomweb.message_service.entity.key.PrivateChatKey;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface PrivateChatRepository extends CassandraRepository<PrivateChat, PrivateChatKey> {

    PrivateChat findByKeyUser1AndKeyUser2(String user1, String user2);
}
