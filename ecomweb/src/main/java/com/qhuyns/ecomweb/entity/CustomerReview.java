package com.qhuyns.ecomweb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerReview {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    int rating;
    String comment;

    @CreationTimestamp
    @Column(updatable = false)
    LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "product_variant_id")
    ProductVariant productVariant;

//    @OneToOne(mappedBy = "customerReview")
//    private OrderItem orderItem;


}