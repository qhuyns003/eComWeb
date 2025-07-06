package com.qhuyns.ecomweb.repository;

import com.qhuyns.ecomweb.entity.ProductVariant;
import com.qhuyns.ecomweb.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ShopRepository extends JpaRepository<Shop, String> {

   @Query("SELECT s FROM Shop s WHERE s.user.username = :username")
   Shop findByUserUsername(@Param("username") String username);
}
