package com.qhuyns.ecomweb.repository;

import com.qhuyns.ecomweb.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, String> {
   List<Cart> findByUserId(String userId);
}
// 4 trang thai entity trong jpa
// transient : duoc tao moi boi new,  chua duoc quan ly
// managed: trang thai dc quan ly , lay ra boi jpa
// detached: tung duo quan ly, khi sesion dong la  k con dc quan ly nua
// removed: entity dc danh dau la se bi xa khi flush

// session cache -> batch -> transaction buffer(db) -> disk(db)
// với Transactional giúp kéo dài session cache suốt method đó nên gom được truy vấn
// khi k có Transactional thì session cache mở và đóng ngay khi hoàn thành
// session (hibernate session) nhừo cơ chế OSIV mà nó được mửo xuyên suốt từ đầu tới cuối rq
// session cache chỉ đc quyền giữ lại các câu lệnh hibernate trong sesion cache nếu nó đc đánh dấu @Transactional