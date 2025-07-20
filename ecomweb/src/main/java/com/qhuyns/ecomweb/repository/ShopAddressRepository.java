package com.qhuyns.ecomweb.repository;

import com.qhuyns.ecomweb.dto.response.ShopAddressResponse;
import com.qhuyns.ecomweb.entity.ShopAddress;
import com.qhuyns.ecomweb.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShopAddressRepository extends JpaRepository<ShopAddress, String> {
    @Query("SELECT sa FROM ShopAddress sa WHERE sa.shop.id IN :shopIds")
    List<ShopAddress> findByShopIds(@Param("shopIds") List<String> shopIds);

}
