package com.ecomweb.identity_service.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
// entity duoc config database co composite indexing voi 2 truong username, active -> tao voi cac truong truy van nhieu, nen tao toi da 3 index
// giup truy van nhanh hon voi username, username+active
// tuy nhien thao tac update insert se cham hon do phai tao ra 2 bang phu va cap nhat 2 bang 1 luc
public class User {
    // luu y, khi quan he bi tach ra, cascade se phai xu ly thu cong khi xoa hoac them moi
    @Id
    String id;
    String username;
    String password;
    LocalDate dob;
    String fullName;
    String email;
    String phone;
    boolean active;

//    @Builder.Default
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    List<Cart> carts= new ArrayList<>();;

//    @Builder.Default
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    List<ShopReview> shopReviews= new ArrayList<>();

//    @Builder.Default
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    List<Order> orders= new ArrayList<>();


//    @Builder.Default
//    @ManyToMany
//    List<Role> roles= new ArrayList<>();

//    @Builder.Default
//    @OneToMany(fetch = FetchType.LAZY,mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    List<UserAddress> userAddresses= new ArrayList<>();


//    @Builder.Default
//    @OneToMany(fetch = FetchType.LAZY,mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<VerificationToken> verificationTokens= new ArrayList<>();

}
