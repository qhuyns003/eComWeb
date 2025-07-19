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
public class UserAddress {
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
    boolean isDefault;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "userAddress", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Order> orders= new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

} 