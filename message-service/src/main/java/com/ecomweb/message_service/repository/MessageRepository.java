package com.ecomweb.message_service.repository;

import com.ecomweb.message_service.entity.Message;
import com.ecomweb.message_service.entity.key.MessagePrimaryKey;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

// cassandra duco cau truc theo dang token ring voi mo hinh p2p - cac node binh dang voi nhau ve vai tro
// moi node se luu tru 1 phan du lieu cua toan the db, quan ly 1 dai so cu the
// so duoc hash ra dua vao parttion key va se vao 1 node phu hop

// cassandra có thể scale ngang nhờ việc thêm bớt node, khi này nó sẽ tự rebalance để tái cấu trúc lại dải số cho node
// thêm node khi dữ liệu trên các node lớn, k cân bằng
// bớt node khi dữ liệu trên các node quá ít -> chi phí vận hành các node tăng, giao thức giưuax các code tăng tính phứuc tạp, mất tgian update node status

// ghi : query -> memtable (nằm trong ram) -> commitlog -> ssTable
// Memtable là bộ cache trên RAM, commitloghoatj đôgnj tương tự journal để đảm bảo rollback dữ liệu nếu crash
// khi bộ nhớ trong memtable đầy nó mới đẩy xuống sstbale(1 file của disk)
// disk có cơ chế hượp nhất các file sstable
// việc ghi nhanh là nhờ nó k cần chờ phải ghi vào đĩa mới thôgn báo tahnfh côgn mà ghi vào RAM(cực nhanh) là xong
public interface MessageRepository extends CassandraRepository<Message, MessagePrimaryKey> {
    List<Message> findByKeyRoomIdOrderByKeySentAtAsc(String roomId);
}
