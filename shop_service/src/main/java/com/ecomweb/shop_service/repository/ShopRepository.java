package com.ecomweb.shop_service.repository;

import com.ecomweb.shop_service.entity.Shop;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShopRepository extends MongoRepository<Shop, String> {

   // tự generate query: { "user.username" : username }
   Shop findByUserUsername(String username);
}
