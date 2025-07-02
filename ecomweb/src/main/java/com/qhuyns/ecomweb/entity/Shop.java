package com.qhuyns.ecomweb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
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
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "shop", cascade = CascadeType.ALL)
    private List<ShopReview> shopReviews;
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "shop", cascade = CascadeType.ALL)
    private List<Product> products;
} 