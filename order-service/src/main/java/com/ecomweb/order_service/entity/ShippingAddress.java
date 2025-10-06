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
public class ShippingAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String receiverName;
    String phoneNumber;
    BigDecimal latitude;
    BigDecimal longitude;
    String fullAddress;
    String detailAddress;
    String ward;
    String wardCode;
    String district;
    Integer districtId;
    String province;
    String provinceId;
    boolean defaultAddress;


    @OneToOne
    @JoinColumn(name = "order_id")
    Order order;

} 