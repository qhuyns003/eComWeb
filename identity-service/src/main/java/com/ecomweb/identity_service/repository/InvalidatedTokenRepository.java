package com.ecomweb.identity_service.repository;

import com.ecomweb.identity_service.entity.InvalidatedToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InvalidatedTokenRepository extends MongoRepository<InvalidatedToken, String> {



}
