package com.qhuyns.ecomweb.repository;

import com.qhuyns.ecomweb.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, String> {
//    @Query("SELECT c FROM Coupon c " +
//            "WHERE c.endDate >= CURRENT_DATE " +
//            "AND c.active = true " +
//            "AND c.requireCode = false " +
//            "AND c.shop.id = :shopId " +
//            "AND NOT EXISTS (SELECT u FROM c.users u WHERE u.username = :userName)")
//    List<Coupon> findAvailableCouponsForShopAndUser(@Param("shopId") String shopId, @Param("userName") String userName);

    @Query("""
    SELECT c FROM Coupon c
    WHERE c.endDate >= CURRENT_DATE
      AND c.active = true
      AND c.requireCode = false
      AND c.shopId = :shopId
      AND c.quantity>c.used
      AND NOT EXISTS (
        SELECT osg FROM c.orderShopGroups osg
        WHERE osg.order.userId = :userId
      )
""")
    List<Coupon> findAvailableCouponsForShopAndUser(@Param("shopId") String shopId, @Param("userId") String userId);

    @Query("""
    SELECT c FROM Coupon c
 
    WHERE c.id IN :ids
      AND c.endDate >= CURRENT_DATE
      AND c.active = true
      AND c.quantity>c.used
      AND c.shopId is null
      AND NOT EXISTS (
        SELECT o FROM c.orders o
        WHERE o.userId = :userId
      )
""")
    List<Coupon> findValidUnusedCouponsOfUser(@Param("userId") String userId, @Param("ids") List<String> ids);
}
