package com.qhuyns.ecomweb.repository;

import com.qhuyns.ecomweb.entity.PrivateChat;
import com.qhuyns.ecomweb.entity.RoomMember;
import com.qhuyns.ecomweb.entity.key.PrivateChatKey;
import com.qhuyns.ecomweb.entity.key.RoomMemberKey;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

public interface PrivateChatRepository extends CassandraRepository<PrivateChat, PrivateChatKey> {

    PrivateChat findByKeyUser1AndKeyUser2(String user1, String user2);
}
