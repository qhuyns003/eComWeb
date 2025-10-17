package com.ecomweb.message_service.repository;

import com.ecomweb.message_service.entity.Message;
import com.ecomweb.message_service.entity.key.MessagePrimaryKey;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

// cassandra duco cau truc theo dang token ring voi mo hinh p2p - cac node binh dang voi nhau ve vai tro
// moi node se luu tru 1 phan du lieu cua toan the db, quan ly 1 dai so cu the
// so duoc hash ra dua vao parttion key va se vao 1 node phu hop
// trong moi node se chua cac partition

// cassandra có thể scale ngang nhờ việc thêm bớt node, khi này nó sẽ tự rebalance để tái cấu trúc lại dải số cho node
// thêm node khi dữ liệu trên các node lớn, k cân bằng
// bớt node khi dữ liệu trên các node quá ít -> chi phí vận hành các node tăng, giao thức giưuax các code tăng tính phứuc tạp, mất tgian update node status

// ghi : query -> commitlog (trc để đảm bảo khi crash thì khôi phục đc ngay) -> memtable (nằm trong ram)  -> ssTable
// Memtable là bộ cache trên RAM, commitloghoatj đôgnj tương tự journal để đảm bảo rollback dữ liệu nếu crash
// khi bộ nhớ trong memtable đầy nó mới đẩy xuống sstbale(1 file của disk)
// disk có cơ chế hợp nhất các file sstable bằng Compaction
// việc ghi nhanh là nhờ nó k cần chờ phải ghi vào đĩa mới thôgn báo tahnfh côgn mà ghi vào RAM(cực nhanh) là xong
// nếu cấu hình sao lưu replica thì có cơ chế tự config cần 50%hay 100% node ghi hoàn tất là trả hoàn tất

// Qua trinh doc trai qua nhieu buoc:
// rq den 1 node bat ki -> node tinh toan vi tri cac du lieu can lay va goi cac node do de lay du lieu
// 1.(option) doc row cache (noi cache du lieu cua cac lan query truoc), dễ bị lỗi thời do nó cache dữ liệu cứng
// 2. doc key cache la noi luu anh xa hásh code den thang du lieu trên sstable, k bị lỗi thời do ánh xạ trực tiếp trên disk
// 2. doc memtable (tra theo parttition key luon, hashcode chi de tim node)
// 3. hoi bloom filter xem du lieu nam tren sstable nao -> doc cac sstable tren dísk
public interface MessageRepository extends CassandraRepository<Message, MessagePrimaryKey> {
    List<Message> findByKeyRoomIdOrderByKeySentAtAsc(String roomId);
}
