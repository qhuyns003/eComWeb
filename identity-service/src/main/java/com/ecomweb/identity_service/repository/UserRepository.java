package com.ecomweb.identity_service.repository;

import com.ecomweb.identity_service.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

// singalone: gửi yêu cầu đơn lẻ -> wiredtiger cache -> ghi log vào journal -> commit vào disk
// replica : các yêu cầu trong 1 transaction tới -> Transaction Manager đánh txd_id xác định id trans
// -> wiredtiger cache -> ghi journal -> Coordinator khi thấy lệnh commit trânsaction flush toàn bộ cache vào dish + ghi là oplog
// -> secondary đọc oplog và đồng bộ dữ liệu theo trình tự nhưu singalone vì cũng có journal + disk

// journal lưu lại db trước và sau có lệnh thao tác -> nếu crash trước commit thì rollback, crassh sau commit thì phục hồi dữ liệu mới
// coordinator điều phối việc ghi oplog, quản lí việc commit hay abort transaction
// oplog ghi trình tụ thay đổi dữ liệu từ primary để các secodary thay đổi theo, nếu 1 node bị lỗi sẽ rollback toàn cụm
public interface UserRepository extends MongoRepository<User, String> {

   Optional<User> findByUsernameAndActive(String username, boolean active);

   Optional<User> findByIdAndActive(String userId, boolean active);

}
