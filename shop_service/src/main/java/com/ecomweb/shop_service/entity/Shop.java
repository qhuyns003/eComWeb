package com.ecomweb.shop_service.entity;

import com.ecomweb.shop_service.entity.ShopAddress;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "shops")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
// embedd snapshot (vi du thong tin user) nhung du lieu it co su thay doi, query nhieu hon la ghi, vi neu ghi nhieu se phai sua nhieu noi
// embedd nhung du lieu khong qua lon (vi du shopaddress 1-1 hoac 1-n vi shop co it address), neu k se phai tach bang vi se bi du lieu phinh to
// k embedd n-n vi khong the truy van nguoc lai duoc
// truy van dua vao 1 dieu kien cua bang khac thi co the nhanh hon mysql, nhung nhieu dk thi chac chan cham hon
public class Shop {

    @Id
    String id;

    String name;
    String description;
    String address;
    String status;

    @CreatedDate
    LocalDateTime createdAt;


    String userId;

    @Builder.Default
    List<String> productIds = new ArrayList<>();

    @Builder.Default
    List<String> shopReviewIds = new ArrayList<>();

    @Builder.Default
    @DBRef
    List<String> orderShopGroupIds = new ArrayList<>();

    @Builder.Default
    List<String> couponIds = new ArrayList<>();

//    @DBRef(lazy = true) k tham chieu, embedd luon
    ShopAddress shopAddress;
}
