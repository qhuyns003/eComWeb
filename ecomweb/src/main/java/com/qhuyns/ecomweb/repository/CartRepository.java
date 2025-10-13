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
