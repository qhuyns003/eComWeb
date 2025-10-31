package com.qhuyns.ecomweb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// builder ínertcode vao class
// ínert them class static va 1 ham builder trả ra đối tuong hàm builder static đó
// cac ham set thuoc tinh trong Builder deu tra ra chinh no -> de co the tiep tuc set
// ham build cua static class se khoi tao class ben ngoai va tra doi tuogn do -> can 1 cóntructor day du tham so cho trg hop nay
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    int quantity;
    LocalDateTime addedAt;

    String userId;

    @ManyToOne
    @JoinColumn(name = "product_variant_id")
    ProductVariant productVariant;
} 