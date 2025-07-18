package com.qhuyns.ecomweb.dto.response;

import com.qhuyns.ecomweb.entity.Order;
import com.qhuyns.ecomweb.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAddressResponse {
    String id;
    String receiverName;
    String phoneNumber;
    BigDecimal latitude;
    BigDecimal longitude;
    String fullAddress;
    String detailAddress;
    String ward;
    String district;
    String province;
    boolean isDefault;

}
