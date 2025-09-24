package com.ecomweb.identity_service.repository;

import com.ecomweb.identity_service.entity.User;
import com.ecomweb.identity_service.entity.UserCoupon;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserCouponRepository extends MongoRepository<UserCoupon, String> {

   List<UserCoupon> findByUserId(String userId);


}
