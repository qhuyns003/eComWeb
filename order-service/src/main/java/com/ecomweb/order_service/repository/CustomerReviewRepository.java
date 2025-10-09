//package com.ecomweb.order_service.repository;
//
//import com.ecomweb.order_service.entity.CustomerReview;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import java.util.List;
//
//public interface CustomerReviewRepository extends JpaRepository<CustomerReview, String> {
//    @Query("""
//        SELECT cr FROM CustomerReview cr
//        LEFT JOIN FETCH cr.orderItem oi
//        LEFT JOIN FETCH oi.orderShopGroup os
//        LEFT JOIN FETCH os.order od
//        WHERE oi.productVariantId IN :ids
//        """)
//    List<CustomerReview> findAllWithUserAndProductVariantAndProductDetail(@Param("ids") List<String> ids);
//
//    @Query("""
//    SELECT cr.rating, COUNT(cr)
//    FROM CustomerReview cr
//    LEFT JOIN cr.orderItem oi
//    WHERE oi.productVariantId IN :ids
//    GROUP BY cr.rating
//    ORDER BY cr.rating DESC
//
//""")
//    List<Object[]> countReviewByRating(@Param("ids") List<String> ids);
//}
