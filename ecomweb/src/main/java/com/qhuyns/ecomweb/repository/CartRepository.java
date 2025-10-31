package com.qhuyns.ecomweb.repository;

import com.qhuyns.ecomweb.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// ke thua thi lop con co toan bo thuco tinh va phuong thuc cua lop cha (tru constructor, static va final)
// tuy nhien de ghi de thi lop con phai duoc phep nhin thay phuong thuc cua lop cha (method khac priavte)
// vi chi khi nhin thay thi lop con moi so sanh duoc va ghi de len duoc -> su dung phuong thuc do cho lop con
// neu la method cha private thi coi nhu method priavte tuogn tu nam o lop con la 1 phuong thuc khac, k dc coi la bi ghi de
// -> de duoc coi la ghi de thi phai la nonstatic + public/protected
// class con sở hữu hàm đó , nhưng hàm đó lại prvate gắn với class Cha nên dù class sở hữu nhưng sẽ k có truyền truy cập
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