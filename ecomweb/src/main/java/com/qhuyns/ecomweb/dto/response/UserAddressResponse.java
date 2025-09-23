package com.qhuyns.ecomweb.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.qhuyns.ecomweb.entity.Order;
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
    String wardCode;
    String district;
    Integer districtId;
    String province;
    Integer provinceId;
    // khi ap sang JSON sẽ tự đôgnj bỏ is -> thêm jsonProp để giữ is

    boolean defaultAddress;

}
