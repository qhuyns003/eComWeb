package com.qhuyns.ecomweb.repository;

import com.qhuyns.ecomweb.entity.Order;
import com.qhuyns.ecomweb.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, String> {

}
