package com.ecomweb.identity_service.repository;

import com.ecomweb.identity_service.entity.UserRole;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRoleRepository extends MongoRepository<UserRole, String> {

   void deleteByUserId(String userId);
}
