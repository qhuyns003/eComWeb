package com.qhuyns.ecomweb.repository;

import com.qhuyns.ecomweb.entity.DetailAttribute;
import com.qhuyns.ecomweb.entity.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetailAttributeRepository extends JpaRepository<DetailAttribute, String> {
    DetailAttribute findByName(String name);
    boolean existsByName(String name);
}
