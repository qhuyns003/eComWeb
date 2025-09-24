package com.ecomweb.shop_service.repository;

import com.ecomweb.shop_service.entity.Shop;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

// mongodb:
//    - truy van single document
//    - de scale khi du lieu lon
//    - schema linh hoat: moi document co the co hoac k co thuco tinh trong collections
//    - khong su dung khi truy van phuc tap tren nhieu bang (join)
// cassandra:
//    - phu hop voi du lieu can doc va ghi toc do cao
//    - toi uu, xay dung table theo truy van, co the bi duplicate -> han che update va delete
//    - han che cac entity co quan he phuc tap -> tao nhieu querry khong kiem soat dc
//    - truy van 2 step van nhanh
// sql:
//    - phu hop voi truy van phuc tap, cho toc do tot
//    - phu hop cho UC can dam bao tinh toan ven du lieu (pay, order)
public interface ShopRepository extends MongoRepository<Shop, String> {

   // tự generate query: { "user.username" : username }
   Optional<Shop> findByUserId(String userId);

   List<Shop> findByIdIn(List<String> ids);

   List<Shop> findByShopAddressProvinceId(String provinceId);

}
