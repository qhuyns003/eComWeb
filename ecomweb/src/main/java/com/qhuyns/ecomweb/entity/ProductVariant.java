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
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    Long price;
    Long stock;
    String status;

    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "product_variant_id"),
            inverseJoinColumns = @JoinColumn(name = "detail_attribute_id")
    )
    List<DetailAttribute> detailAttributes= new ArrayList<>();;

    @ManyToOne
    @JoinColumn(name = "product_id")
    Product product;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "productVariant", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems= new ArrayList<>();;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "productVariant", cascade = CascadeType.ALL)
    private List<Cart> carts= new ArrayList<>();;
}