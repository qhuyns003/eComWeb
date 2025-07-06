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
public class DetailAttribute {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String name;
    String status;

    @ManyToOne
    @JoinColumn(name = "product_attribute_id")
    ProductAttribute productAttribute;
    //set list quan he phia ben khong co mapped By moi luu duoc quan he
    @Builder.Default
    @ManyToMany(mappedBy = "detailAttributes")
    List<ProductVariant> productVariants= new ArrayList<>();;
}