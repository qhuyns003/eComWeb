package com.qhuyns.ecomweb.repository;

import com.qhuyns.ecomweb.entity.Category;
import com.qhuyns.ecomweb.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserAddressRepository extends JpaRepository<UserAddress, String> {
    List<UserAddress> findByUser_Id(String id);
    @Query("SELECT ua FROM UserAddress ua JOIN ua.user u WHERE u.username = :username")
    List<UserAddress> findAllByUsername(@Param("username") String username);

}
