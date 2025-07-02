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
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    Long price;
    Long stock;

    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "product_variant_id"),
            inverseJoinColumns = @JoinColumn(name = "detail_attribute_id")
    )
    List<DetailAttribute> detailAttributes;

    @ManyToOne
    @JoinColumn(name = "product_id")
    Product product;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "productVariant", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "productVariant", cascade = CascadeType.ALL)
    private List<Cart> carts;
}