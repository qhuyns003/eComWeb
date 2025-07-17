package com.qhuyns.ecomweb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderShopGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Enumerated(EnumType.STRING)
    Shipment shipment;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    Shop shop;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "orderShopGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    List<OrderItem> orderItems= new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "order_id")
    Order order;
} 