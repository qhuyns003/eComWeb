package com.ecomweb.order_service.entity;

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

    String productVariantId;


    String customerReviewId;
} 