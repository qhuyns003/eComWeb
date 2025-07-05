package com.qhuyns.ecomweb.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String code;
    BigDecimal discount;
    BigDecimal minOrder;
    LocalDate startDate;
    LocalDate endDate;
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "coupon", cascade = CascadeType.ALL)
    private List<UserCoupon> userCoupons= new ArrayList<>();;
} 