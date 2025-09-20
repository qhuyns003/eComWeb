package com.ecomweb.user_service.repository;

import com.ecomweb.user_service.entity.Role;
import com.ecomweb.user_service.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {

   Optional<Role> findById(String id);

}
