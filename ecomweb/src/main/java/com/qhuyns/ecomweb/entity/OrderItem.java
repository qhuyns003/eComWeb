package com.qhuyns.ecomweb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    int quantity;
    BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "order_shop_group_id")
    OrderShopGroup orderShopGroup;

    @ManyToOne
    @JoinColumn(name = "product_variant_id")
    ProductVariant productVariant;


    @OneToOne
    @JoinColumn(name = "customer_review_id")
    private CustomerReview customerReview;
} 