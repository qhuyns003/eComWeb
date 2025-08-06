package com.qhuyns.ecomweb.repository;

import com.qhuyns.ecomweb.dto.request.ProductFilterRequest;
import com.qhuyns.ecomweb.entity.Category;
import com.qhuyns.ecomweb.entity.OrderStatus;
import com.qhuyns.ecomweb.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {
    // native query khong ho tro lay danh sach líst quan he nhu JPQL hay JPA
    // native phai mapping thu cong cac thuoc tinh vao entity khong dung p.* duoc neu nhu co tinh count hay avg , ...
    // chi mapping duoc khi chi lay nguyen entity select p From Product p
    // JPQL dua vao entity con sql native dua vao db thuc te
    // Fetch join và tính toán aggregate không thể dùng chung trong 1 query JPQL.
    // Han che tinh toan o service neu co the tinh toan duoc o db de toi uu hieu nang do khong can phai load du lieu vao mem
    // 1 obj[] la 1 ban ghi
    @Query(
            value = """
                 SELECT p.*, COUNT(oi.id) as order_count, ROUND(AVG(cr.rating),1) as rating
                 FROM product p
                 LEFT JOIN order_item oi ON oi.product_id = p.id
                 LEFT JOIN customer_review cr ON cr.product_id = p.id
                 GROUP BY p.id
                 ORDER BY COUNT(oi.id) DESC
                 LIMIT :limit
        """,
            nativeQuery = true
    )
    List<Object[]> findTopSellingProductsNative(@Param("limit") int limit);

    @Query("""
    SELECT p, COUNT(oi), COALESCE(ROUND(AVG(cr.rating), 1), 0)
    FROM Product p
    LEFT JOIN p.productVariants v
    LEFT JOIN v.orderItems oi
    LEFT JOIN oi.customerReview cr
    LEFT JOIN oi.orderShopGroup osg
    LEFT JOIN osg.order o
    WHERE o.status = com.qhuyns.ecomweb.entity.OrderStatus.PAID OR o.id IS NULL
    GROUP BY p
    ORDER BY COUNT(oi) DESC
""")
    List<Object[]> findTopSellingProducts(Pageable pageable);

    @Query("""
    SELECT p, COUNT(oi), COALESCE(ROUND(AVG(cr.rating), 1), 0)
    FROM Product p
    LEFT JOIN p.productVariants v
    LEFT JOIN v.orderItems oi
    LEFT JOIN oi.customerReview cr
    GROUP BY p
    ORDER BY p.createdAt DESC
""")
    List<Object[]> findNewestProducts(Pageable pageable);

    // fetch de lay ca danh sach quan he
    @Query("""
    SELECT p
    FROM Product p
    LEFT JOIN FETCH p.shop s
    LEFT JOIN FETCH p.images imgs
    LEFT JOIN FETCH p.productVariants v
    LEFT JOIN FETCH v.detailAttributes da
    LEFT JOIN FETCH p.productAttributes pa
    WHERE p.id = :id
""")
    Product findProductDetailById(@Param("id") String id);

    @Query("""
    SELECT COUNT(oi), COALESCE(AVG(cr.rating), 0)
    FROM Product p
    LEFT JOIN p.productVariants v
    LEFT JOIN v.orderItems oi
    LEFT JOIN oi.customerReview cr
    LEFT JOIN oi.orderShopGroup osg
    LEFT JOIN osg.order o
    WHERE p.id = :id and o.status = :status
""")
    Object[] findNumberOfOrderAndRating(@Param("id") String id,@Param("status") OrderStatus status);
 // khong can fetch de lay quan he, select luon anh cho nhanh

    @Query(
            value = """
        SELECT p, img
        FROM Product p
        JOIN p.shop s
        JOIN s.user u
        LEFT JOIN p.images img WITH img.isMain = true
        WHERE u.id = :userId
          AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:status IS NULL OR p.status = :status)
        """,
            countQuery = """
        SELECT COUNT(p)
        FROM Product p
        JOIN p.shop s
        JOIN s.user u
        WHERE u.id = :userId
          AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:status IS NULL OR p.status = :status)
        """
    )
    Page<Object[]> findProductsWithMainImageByUserId(
            @Param("userId") String userId,
            @Param("search") String search,
            @Param("status") Integer status,
            Pageable pageable
    );

    @Query(
            value = """
        SELECT p, img
        FROM Product p
        LEFT JOIN p.images img WITH img.isMain = true
        WHERE
          (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:status IS NULL OR p.status = :status)
        """,
            countQuery = """
        SELECT COUNT(p)
        FROM Product p
        WHERE 
          (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:status IS NULL OR p.status = :status)
        """
    )
    Page<Object[]> searchProduct(
            @Param("search") String search,
            @Param("status") Integer status,
            Pageable pageable,
            @Param("productFilterRequest") ProductFilterRequest productFilterRequest
    );

    void deleteByIdIn(List<String> ids);

    @Query("SELECT COUNT(oi) > 0 FROM OrderItem oi WHERE oi.productVariant.product.id = :productId")
    boolean existsOrderForProduct(@Param("productId") String productId);

}
