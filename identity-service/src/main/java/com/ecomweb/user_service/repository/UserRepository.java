package com.ecomweb.user_service.repository;

import com.ecomweb.user_service.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

   Optional<User> findByUsernameAndActive(String username, boolean active);

   Optional<User> findByIdAndActive(String userId, boolean active);

}
