package com.qhuyns.ecomweb.repository;

import com.qhuyns.ecomweb.entity.Message;
import com.qhuyns.ecomweb.entity.Room;
import com.qhuyns.ecomweb.entity.key.MessagePrimaryKey;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

public interface RoomRepository extends CassandraRepository<Room, String> {
}
