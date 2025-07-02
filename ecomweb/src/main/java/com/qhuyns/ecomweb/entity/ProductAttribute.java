package com.qhuyns.ecomweb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductAttribute {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String name;



    @OneToMany(fetch = FetchType.LAZY, mappedBy = "productAttribute", cascade = CascadeType.ALL)
    List<DetailAttribute> detailAttributes;

    @ManyToMany(mappedBy = "productAttributes")
    List<Product> products;

} 