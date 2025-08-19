package com.qhuyns.ecomweb.repository;

import com.qhuyns.ecomweb.entity.Room;
import com.qhuyns.ecomweb.entity.UserRoom;
import com.qhuyns.ecomweb.entity.key.UserRoomKey;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UserRoomRepository extends CassandraRepository<UserRoom, UserRoomKey> {
    List<UserRoom> findByKeyUserIdOrderByKeyLastMessageAtDesc(String keyUserId);
    List<UserRoom> findByKeyUserId(String  keyUserId);
    UserRoom findByKeyUserIdAndKeyLastMessageAtAndKeyRoomId(String  keyUserId, LocalDateTime lastMessageAt, String  keyRoomId);
}
