package com.ecomweb.user_service.repository;

import com.ecomweb.user_service.entity.User;
import com.ecomweb.user_service.entity.UserRole;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRoleRepository extends MongoRepository<UserRole, String> {

   void deleteByUserId(String userId);
}
