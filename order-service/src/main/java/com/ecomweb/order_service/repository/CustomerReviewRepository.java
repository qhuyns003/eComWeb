package com.ecomweb.order_service.repository;

import com.ecomweb.order_service.entity.CustomerReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerReviewRepository extends JpaRepository<CustomerReview, String> {
    @Query("""
        SELECT cr FROM CustomerReview cr
        LEFT JOIN FETCH cr.orderItem oi
        LEFT JOIN FETCH oi.orderShopGroup os
        LEFT JOIN FETCH os.order od
        LEFT JOIN FETCH oi.productVariant pv
        LEFT JOIN  pv.product p
        LEFT JOIN FETCH pv.detailAttributes da
        WHERE p.id = :id
        """)
    List<CustomerReview> findAllWithUserAndProductVariantAndProductDetail(@Param("id") String id);

    @Query("""
    SELECT cr.rating, COUNT(cr)
    FROM CustomerReview cr
    LEFT JOIN cr.orderItem oi
    LEFT JOIN oi.productVariant pv
    LEFT JOIN  pv.product p
    WHERE p.id = :id
    GROUP BY cr.rating
    ORDER BY cr.rating DESC
    
""")
    List<Object[]> countReviewByRating(@Param("id") String id);
}
