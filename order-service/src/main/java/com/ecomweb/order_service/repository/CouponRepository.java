package com.ecomweb.order_service.repository;

import com.ecomweb.order_service.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// ORM - Object Relational Mapping : ki thuat anh xa table va entity -> JPA la 1 ORM

// - jdbc : tự quản lý kết nối, query, mapping thủ công 100%
// - jpa : la 1 chuan, form duoc thong nhat giua jpa va hibernate de co the chuyen querry thanh cau lenh aql de hibernate xu li truy van va tra ket qua
// - các lệnh tương tác với db ở jpa chỉ xoay quanh các format đã định sẵn như persist, merge, createQuerry,...
// - Entitty Manager là lớp điều khiển duy nhất trong jpa <-> DB
// - JPA hơn jdbc o cho k can mapping thu cong va quan ly ke noi, nhan ket qua thu cong cho tung truy van
// - SD jpa : tuong tự jpa nhưng có thêm chức năng custome 1 hàm trong repo -> các hàm sẽ được biên dịch thành câu lệnh jpa tương ứng và đưa hbernate xử lí
// - SDJ <-> JPA <-> Hibernate <-> DB
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
