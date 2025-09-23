package com.qhuyns.ecomweb.repository;

import com.qhuyns.ecomweb.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {
}
