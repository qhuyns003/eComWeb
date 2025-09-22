package com.ecomweb.identity_service.repository;

import com.ecomweb.identity_service.entity.User;
import com.ecomweb.identity_service.entity.UserRole;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRoleRepository extends MongoRepository<UserRole, String> {

   void deleteByUserId(String userId);
   List<UserRole> findByUserId(String userId);
}
