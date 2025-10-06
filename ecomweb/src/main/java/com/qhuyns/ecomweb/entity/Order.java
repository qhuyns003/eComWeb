//package com.qhuyns.ecomweb.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//import lombok.experimental.FieldDefaults;
//import org.hibernate.annotations.CreationTimestamp;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@FieldDefaults(level = AccessLevel.PRIVATE)
//@Table(name = "orders")
//public class Order {
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    String id;
//
//    @Enumerated(EnumType.STRING)
//    OrderStatus status;
//    // nen luu de tranh viec moi khi truy van phai tinh toan
//    BigDecimal total;
//    BigDecimal shippingFee;
//    BigDecimal subtotal;
//    BigDecimal totalDiscount;
//
//
//    @CreationTimestamp
//    @Column(updatable = false)
//    LocalDateTime createdAt;
//
//    String userId;
//
//    @Builder.Default
//    @ManyToMany
//    @JoinTable(
//            joinColumns = @JoinColumn(name = "order_id"),
//            inverseJoinColumns = @JoinColumn(name = "coupon_id")
//    )
//    List<Coupon> coupons= new ArrayList<>();
//
//    @Builder.Default
//    @OneToMany(fetch = FetchType.LAZY,mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
//    List<OrderShopGroup> orderShopGroups= new ArrayList<>();
//
//    @OneToOne(fetch = FetchType.LAZY, mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
//    ShippingAddress shippingAddress;
//
//    @Enumerated(EnumType.STRING)
//    Payment payment;
//
//
//}