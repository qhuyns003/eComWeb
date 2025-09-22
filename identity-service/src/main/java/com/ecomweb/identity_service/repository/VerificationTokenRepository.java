package com.ecomweb.identity_service.repository;

import com.ecomweb.identity_service.entity.VerificationToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface VerificationTokenRepository extends MongoRepository<VerificationToken, String> {

   List<VerificationToken> findByUserId(String userId);

}
