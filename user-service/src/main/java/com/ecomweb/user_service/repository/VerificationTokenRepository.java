package com.ecomweb.user_service.repository;

import com.ecomweb.user_service.entity.Role;
import com.ecomweb.user_service.entity.VerificationToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface VerificationTokenRepository extends MongoRepository<VerificationToken, String> {

   List<VerificationToken> findByUserId(String userId);

}
