package com.qhuyns.ecomweb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
// entity duoc config database co composite indexing voi 2 truong username, active -> tao voi cac truong truy van nhieu, nen tao toi da 3 index
// giup truy van nhanh hon voi username, username+active
// tuy nhien thao tac update insert se cham hon do phai tao ra 2 bang phu va cap nhat 2 bang 1 luc
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "username", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String username;

    String password;
    LocalDate dob;
    String fullName;
    String email;
    String phone;
    boolean active;

    @Builder.Default
    @ManyToMany(mappedBy = "users")
    List<Coupon> coupons= new ArrayList<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Cart> carts= new ArrayList<>();;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ShopReview> shopReviews= new ArrayList<>();;
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
//    List<CustomerReview> customerReviews;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Order> orders= new ArrayList<>();;
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    Shop shop;

    @Builder.Default
    @ManyToMany
    List<Role> roles= new ArrayList<>();;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<UserAddress> userAddresses= new ArrayList<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Notification> notificationsSent = new ArrayList<>();



    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "recipient", cascade = CascadeType.ALL, orphanRemoval = true)
    List<NotificationRecipient> notificationRecipients = new ArrayList<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VerificationToken> verificationTokens= new ArrayList<>();

}
