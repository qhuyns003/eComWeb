package com.ecomweb.message_service.repository;

import com.ecomweb.message_service.entity.Room;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface RoomRepository extends CassandraRepository<Room, String> {
    Room findByRoomId(String roomId);
}
