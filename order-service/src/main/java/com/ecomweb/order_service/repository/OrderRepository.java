package com.ecomweb.order_service.repository;

import com.ecomweb.order_service.entity.Order;
import com.ecomweb.order_service.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {

    @Query("""
    SELECT COUNT(oi), COALESCE(AVG(cr.rating), 0)
    FROM OrderItem oi
    LEFT JOIN oi.customerReview cr
    LEFT JOIN oi.orderShopGroup osg
    LEFT JOIN osg.order o
    WHERE  o.status = :status AND oi.productVariantId IN (:ids)
""")
    Object[] findNumberOfOrderAndRating(@Param("ids") List<String> ids, @Param("status") OrderStatus status);

}
