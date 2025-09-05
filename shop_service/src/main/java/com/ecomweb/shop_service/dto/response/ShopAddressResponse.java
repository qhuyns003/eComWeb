package com.ecomweb.shop_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShopAddressResponse {
    String id;
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

}
