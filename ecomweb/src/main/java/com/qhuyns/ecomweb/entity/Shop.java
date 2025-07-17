package com.qhuyns.ecomweb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String name;
    String description;
    String address;
    String status;

    @CreationTimestamp
    @Column(updatable = false)
    LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "user_id")
    User user;
    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShopReview> shopReviews= new ArrayList<>();;
    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products= new ArrayList<>();
    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderShopGroup> orderShopGroups= new ArrayList<>();;
} 