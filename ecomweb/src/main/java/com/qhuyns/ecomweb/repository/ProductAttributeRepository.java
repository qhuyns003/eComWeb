package com.qhuyns.ecomweb.repository;

import com.qhuyns.ecomweb.entity.Product;
import com.qhuyns.ecomweb.entity.ProductAttribute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, String> {

    boolean existsByNameAndProductId(String name,String productId);
    ProductAttribute findByNameAndProductId(String name,String productId);
    void deleteByProductIdAndNameNotIn(String productId, List<String> names);

    @Modifying
    @Transactional
    @Query("UPDATE ProductAttribute pa SET pa.status = '0' WHERE pa.product.id = :productId AND pa.name NOT IN :names")
    void softDeleteByProductIdAndNameNotIn(@Param("productId") String productId, @Param("names") List<String> names);

    List<ProductAttribute> findByProductIdAndStatus(String productId,String status);
}
