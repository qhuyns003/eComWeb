package com.qhuyns.ecomweb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
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

    @CreationTimestamp
    @Column(updatable = false)
    LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;


    @OneToMany(fetch = FetchType.LAZY,mappedBy = "order", cascade = CascadeType.ALL)
    List<OrderItem> orderItems= new ArrayList<>();;
    @Enumerated(EnumType.STRING)
    Shipment shipment;
    @Enumerated(EnumType.STRING)
    Payment payment;
} 