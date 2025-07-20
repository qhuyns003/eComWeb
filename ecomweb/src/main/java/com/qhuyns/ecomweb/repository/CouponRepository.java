package com.qhuyns.ecomweb.repository;

import com.qhuyns.ecomweb.entity.Category;
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
      AND c.shop.id = :shopId
      AND c.quantity>c.used
      AND NOT EXISTS (
        SELECT osg FROM c.orderShopGroups osg
        WHERE osg.order.user.username = :userName
      )
""")
    List<Coupon> findAvailableCouponsForShopAndUser(@Param("shopId") String shopId, @Param("userName") String userName);

    @Query("""
    SELECT c FROM Coupon c
    JOIN c.users u
    WHERE u.username = :userName
      AND c.endDate >= CURRENT_DATE
      AND c.active = true
      AND c.quantity>c.used
      AND c.shop is null
      AND NOT EXISTS (
        SELECT o FROM c.orders o
        WHERE o.user.username = :userName
      )
""")
    List<Coupon> findValidUnusedCouponsOfUser(@Param("userName") String userName);
}
