package com.qhuyns.ecomweb.repository;

import com.qhuyns.ecomweb.entity.Category;
import com.qhuyns.ecomweb.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, String> {
    @Query("SELECT c FROM Coupon c " +
            "WHERE c.endDate >= CURRENT_DATE " +
            "AND c.active = true " +
            "AND c.requireCode = false " +
            "AND c.shop.id = :shopId " +
            "AND NOT EXISTS (SELECT u FROM c.users u WHERE u.username = :userName)")
    List<Coupon> findAvailableCouponsForShopAndUser(@Param("shopId") String shopId, @Param("userName") String userName);
}
