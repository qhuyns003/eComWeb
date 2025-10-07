package com.qhuyns.ecomweb.repository;

import com.qhuyns.ecomweb.dto.request.ProductFilterRequest;
import com.qhuyns.ecomweb.entity.Category;
import com.qhuyns.ecomweb.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.util.List;

// hibernate là trái tim của jpa, giúp chuyển jpq,format sang ngon ngu sql, goi truy van xuong db, tra du lieu
// jpa don gian chi la 1 tap interface quy dinh cac annotation, cac ham persist,.. de hibernate implement no va thuc hien
// vd : trong code ta chi khai bao ham, viet jpql (jpa interface) -> hibernate se thuc hien chung
// spring data jpa chi ho tro rdbms nhu mysql,h2,...
// jpa noi chung chi ho tro rdbms (ho tro cac annotation jakarta), k ho tro nosql
// cac thu vien cua jpa la bao gom jpa + hbernate roi, con khai niem jpa bthg chi la 1 interface bthg de hibernate implement
// cac nosql khac k dung hibernate  ma dung 1 driver cua rieng no de compiler format thanh cau lenh querry tuong tu hibernate


// cac query co the duco dung indexing voi thuoc tinh thuoc tap con cua index
// vi du index abc
// query a ab abc abe ade, khong duoc b bc c
// co che merge index nhung se rat cham hon so voi composite index, cac fk index thuong se dc auto merge
// product join cate, index composite tren thuco tinh cua prod, con cate index voi dieu kien loc + prodid
// fulltext index la cơ chế riêng,  theo btree, k tham gia merge, trong khi merge yêu cầu ctdl btree
// dung fultext la k dung duco index khac nua trong query cua bang do
// optimazer sql se chi chon 1 index toi uu nhat de su dung (tru truong hop merge duoc nhung it)
public interface ProductRepository extends JpaRepository<Product, String> {
    // native query khong ho tro lay danh sach líst quan he nhu JPQL hay JPA
    // native mặc định trải các thuộc tính p.* và img.* ra thành các cột và bắt map thủ công, chỉ map entity đc nếu select thuôcj tính 1 bảng
    // -> do chỉ trả ra record nên mới k lấy được danh sách đối tượng quan hệ
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

    @Query("""
    SELECT p, COUNT(oi), COALESCE(ROUND(AVG(cr.rating), 1), 0)
    FROM Product p
    LEFT JOIN p.productVariants v
    LEFT JOIN v.orderItems oi
    LEFT JOIN oi.customerReview cr
    WHERE p.shopId = :shopId
    AND p.status = '1'
    GROUP BY p
    ORDER BY p.createdAt DESC
""")
    List<Object[]> findByShopIdAndNewest(Pageable pageable,@Param("shopId") String shopId);

    // fetch de lay ca danh sach quan he
    @Query("""
    SELECT p
    FROM Product p
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
        LEFT JOIN p.images img WITH img.isMain = true
        WHERE p.shopId = :shopId
          AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:status IS NULL OR p.status = :status)
        """,
            countQuery = """
        SELECT COUNT(p)
        FROM Product p
        WHERE p.shopId = :shopId
          AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:status IS NULL OR p.status = :status)
        """
    )
    Page<Object[]> findProductsWithMainImageByShopId(
            @Param("shopId") String shopId,
            @Param("search") String search,
            @Param("status") Integer status,
            Pageable pageable
    );
// like "%%" -> scan, k index duoc
// dung fulltext index r k dung composite dc nua
// fulltext tim kiem theo tung tu 1, chia cac tu trong 1 cau ra
    // fultext co the truy van tren nhieu cot la text
    // jpql k ho tro dung fulltext nhu native
    @Query(
            value = """
            SELECT p.id, p.name, p.description, p.price, p.status, p.created_at, img.id, img.url, img.is_main
            FROM product p
            LEFT JOIN product_image img ON img.product_id = p.id AND img.is_main = true
            WHERE
             (:shopIds IS NULL OR p.shop_id IN (:shopIds)) AND
              (:search IS NULL OR MATCH(p.name) AGAINST (:search IN NATURAL LANGUAGE MODE))
              AND (:status IS NULL OR p.status = :status)
              AND (:#{#productFilterRequest.categoryId} IS NULL OR p.category_id = :#{#productFilterRequest.categoryId})
              AND (:#{#productFilterRequest.minPrice} IS NULL OR p.price >= :#{#productFilterRequest.minPrice})
              AND (:#{#productFilterRequest.maxPrice} IS NULL OR p.price <= :#{#productFilterRequest.maxPrice})
            """,
            countQuery = """
            SELECT COUNT(*)
            FROM product p
            WHERE
             (:shopIds IS NULL OR p.shop_id IN (:shopIds)) AND
              (:search IS NULL OR MATCH(p.name) AGAINST (:search IN NATURAL LANGUAGE MODE))
              AND (:status IS NULL OR p.status = :status)
              AND (:#{#productFilterRequest.categoryId} IS NULL OR p.category_id = :#{#productFilterRequest.categoryId})
              AND (:#{#productFilterRequest.minPrice} IS NULL OR p.price >= :#{#productFilterRequest.minPrice})
              AND (:#{#productFilterRequest.maxPrice} IS NULL OR p.price <= :#{#productFilterRequest.maxPrice})
            """,
            nativeQuery = true
    )
    Page<Object[]> searchProduct(
            @Param("search") String search,
            @Param("status") String status,
            Pageable pageable,
            @Param("productFilterRequest") ProductFilterRequest productFilterRequest,
            @Param("shopIds") List<String> shopIds
    );


    void deleteByIdIn(List<String> ids);

    @Query("SELECT COUNT(oi) > 0 FROM OrderItem oi WHERE oi.productVariant.product.id = :productId")
    boolean existsOrderForProduct(@Param("productId") String productId);

}
