package com.qhuyns.ecomweb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
// khong dung data do toString se khien cho code bi lap vo han do manytoone va onetomany duoc in ra
// log ra cung thay lap vo han nhung khong sao vi obj se tham chieu lan nhau -> khong anh huong hieu nang
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String name;
    String description;
    BigDecimal price;
    String status;

    @CreationTimestamp
    @Column(updatable = false)
    LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    Shop shop;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ProductImage> images = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product", cascade = CascadeType.ALL)
    List<ProductVariant> productVariants= new ArrayList<>();;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product", cascade = CascadeType.ALL)
    List<ProductAttribute> productAttributes= new ArrayList<>();;

} 