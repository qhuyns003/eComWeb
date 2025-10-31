package com.qhuyns.ecomweb.repository;

import com.qhuyns.ecomweb.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {
}
// code trong nay duoc hibernate sinh ra se k dc luu trong tarrget vi no chi dc luu vao trong runtime memory
// cac phuong thuc mac dinh ta thay la do cua JpaRepository dc extend