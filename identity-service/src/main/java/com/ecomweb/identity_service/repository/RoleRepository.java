package com.ecomweb.identity_service.repository;

import com.ecomweb.identity_service.entity.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {

   Optional<Role> findById(String id);

}
