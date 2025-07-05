package com.qhuyns.ecomweb.repository;

import com.qhuyns.ecomweb.entity.Category;
import com.qhuyns.ecomweb.entity.Product;
import com.qhuyns.ecomweb.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, String> {
    void deleteById(String id);
    List<ProductImage> findByProductId(String productId);
}
