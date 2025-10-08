package com.qhuyns.ecomweb.repository;

import com.qhuyns.ecomweb.entity.ProductAttribute;
import com.qhuyns.ecomweb.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, String> {
    void deleteByIdNotIn(List<String> ids);

    @Modifying
    @Transactional
    @Query("UPDATE ProductVariant pv " +
            "SET pv.status = '0' " +
            "WHERE pv.id NOT IN :ids " )
    void softDeleteByIdNotIn(

            @Param("ids") List<String> ids
    );

    @Query("SELECT pv FROM ProductVariant pv " +
            "JOIN pv.detailAttributes da " +
            "WHERE da.name = :detailName " +
            "AND da.productAttribute.name = :attributeName " +
            "AND pv.product.id = :productId")
    ProductVariant findOneByDetailNameAndAttributeNameAndProductId(
            @Param("detailName") String detailName,
            @Param("attributeName") String attributeName,
            @Param("productId") String productId
    );

    @Query("SELECT COUNT(pv) > 0 " +
            "FROM ProductVariant pv " +
            "JOIN pv.detailAttributes da " +
            "WHERE da.name = :detailName " +
            "AND da.productAttribute.name = :attributeName " +
            "AND pv.product.id = :productId")
    boolean existsByDetailNameAndAttributeNameAndProductId(
            @Param("detailName") String detailName,
            @Param("attributeName") String attributeName,
            @Param("productId") String productId
    );

    @Query("""
            SELECT pv FROM ProductVariant pv
            JOIN pv.detailAttributes da
            WHERE pv.product.id = :productId
            AND da.id IN :detailAttributeIds
            GROUP BY pv
            HAVING COUNT(da) = :size
            """)
            ProductVariant findByProductIdAndDetailAttributeIds(
            @Param("productId") String productId,
            @Param("detailAttributeIds") List<String> detailAttributeIds,
            @Param("size") long size
    );

    List<ProductVariant> findByProductIdAndStatus(String productId, String status);

    List<ProductVariant> findByProductId(String productId);
}
