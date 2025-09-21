package com.ecomweb.user_service.repository;

import com.ecomweb.user_service.entity.User;
import com.ecomweb.user_service.entity.UserAddress;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

// chi dung multi step trong truog hop truy van dieu kien tren toi da 1 bang phu
// con k thi phai dung lookup neu khong phai intersection cac ket qua rat phuc tap ma performance kem hon
public interface UserAddressRepository extends MongoRepository<UserAddress, String> {

   List<UserAddress> findAllByUserId(String userId);
}
