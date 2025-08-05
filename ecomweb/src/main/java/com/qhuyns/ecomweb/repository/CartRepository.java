package com.qhuyns.ecomweb.repository;

import com.qhuyns.ecomweb.entity.Cart;
import com.qhuyns.ecomweb.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, String> {
   List<Cart> findByUserId(String userId);
}
