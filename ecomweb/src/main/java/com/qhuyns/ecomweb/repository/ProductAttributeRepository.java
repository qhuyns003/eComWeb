package com.qhuyns.ecomweb.repository;

import com.qhuyns.ecomweb.entity.Product;
import com.qhuyns.ecomweb.entity.ProductAttribute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, String> {

    boolean existsByName(String name);
    ProductAttribute findByName(String name);
}
