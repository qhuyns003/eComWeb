package com.qhuyns.ecomweb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String name;
    String description;
    BigDecimal price;
    int stock;
    String status;
    LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    Shop shop;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product", cascade = CascadeType.ALL)
    List<ProductImage> images;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product", cascade = CascadeType.ALL)
    List<ProductAttribute> attributes;
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "product", cascade = CascadeType.ALL)
    private List<Cart> carts;
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "product", cascade = CascadeType.ALL)
    private List<CustomerReview> customerReviews;
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "product", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;
} 