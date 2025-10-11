package com.ecomweb.order_service.repository;

import com.ecomweb.order_service.entity.Coupon;
import com.ecomweb.order_service.entity.Order;
import com.ecomweb.order_service.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, String> {


    @Query("SELECT COUNT(oi) > 0 FROM OrderItem oi WHERE oi.productVariantId IN :ids")
    boolean existsOrderForProduct(@Param("ids") List<String> ids);

    @Query("""
    SELECT COUNT(oi) FROM OrderItem oi
    WHERE oi.productVariantId IN :ids
""")
    Long getNumberOfOrder(@Param("ids") List<String> ids);

    Optional<OrderItem> findByCustomerReviewId(String id);
}
