package com.qhuyns.ecomweb.repository;

import com.qhuyns.ecomweb.entity.RoomMember;
import com.qhuyns.ecomweb.entity.UserRoom;
import com.qhuyns.ecomweb.entity.key.RoomMemberKey;
import com.qhuyns.ecomweb.entity.key.UserRoomKey;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

public interface RoomMemberRepository extends CassandraRepository<RoomMember, RoomMemberKey> {
    List<RoomMember> findByKeyRoomId(String roomId);
}
