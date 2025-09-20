package com.ecomweb.user_service.entity;


import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Document(collection = "user-coupon")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCoupon {
    String userId;
    String couponId;
}
