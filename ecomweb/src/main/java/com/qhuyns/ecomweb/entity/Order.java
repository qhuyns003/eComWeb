package com.qhuyns.ecomweb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String status;
    BigDecimal total;
    LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User customer;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    Shop shop;


    @OneToMany(fetch = FetchType.LAZY,mappedBy = "order", cascade = CascadeType.ALL)
    List<OrderItem> orderItems;
    Shipment shipment;
    Payment payment;
} 