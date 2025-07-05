package com.qhuyns.ecomweb.repository;

import com.qhuyns.ecomweb.entity.DetailAttribute;
import com.qhuyns.ecomweb.entity.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DetailAttributeRepository extends JpaRepository<DetailAttribute, String> {
    DetailAttribute findByNameAndProductAttributeId(String name,String productAttributeId);
    boolean existsByNameAndProductAttributeId(String name,String productAttributeId);
    void deleteByProductAttributeIdAndNameNotIn(String productAttributeId,List<String> names);

    @Query("SELECT d FROM DetailAttribute d " +
            "WHERE d.name = :detailName " +
            "AND d.productAttribute.name = :attributeName " +
            "AND d.productAttribute.product.id = :productId")
    DetailAttribute findByProductIdAndAttributeNameAndDetailName(
            @Param("productId") String productId,
            @Param("attributeName") String attributeName,
            @Param("detailName") String detailName
    );

//    @Modifying
//    @Transactional
//    @Query("UPDATE DetailAttribute da " +
//            "SET da.status = '0' " +
//            "WHERE da.productAttribute.id = :productAttributeId " +
//            "AND da.productAttribute.product.id = :productId " +
//            "AND da.name NOT IN :names")
//    void softDeleteByProductAttributeIdAndNameNotInAndProductId(
//            @Param("productAttributeId") String productAttributeId,
//            @Param("productId") String productId,
//            @Param("names") List<String> names
//    );
    List<DetailAttribute> findByProductAttributeIdAndStatus(String productAttributeId,String status);
    @Modifying
    @Transactional
    @Query(value = "UPDATE detail_attribute da " +
            "JOIN product_attribute pa ON pa.id = da.product_attribute_id " +
            "SET da.status = '0' " +
            "WHERE da.product_attribute_id = :productAttributeId " +
            "AND pa.product_id = :productId " +
            "AND da.name NOT IN (:names)", nativeQuery = true)
    void softDeleteByProductAttributeIdAndNameNotInAndProductId(
            @Param("productAttributeId") String productAttributeId,
            @Param("productId") String productId,
            @Param("names") List<String> names
    );
}

