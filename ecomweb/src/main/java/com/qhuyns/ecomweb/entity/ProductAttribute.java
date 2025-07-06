package com.qhuyns.ecomweb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductAttribute {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String name;
    String status;


    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "productAttribute", cascade = CascadeType.ALL, orphanRemoval = true)
    List<DetailAttribute> detailAttributes= new ArrayList<>();;

    @ManyToOne
    @JoinColumn(name = "product_id")
    Product product;

} 