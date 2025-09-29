package com.ecomweb.message_service.repository;

import com.ecomweb.message_service.entity.RoomMember;
import com.ecomweb.message_service.entity.key.RoomMemberKey;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

public interface RoomMemberRepository extends CassandraRepository<RoomMember, RoomMemberKey> {
    List<RoomMember> findByKeyRoomId(String roomId);
}
