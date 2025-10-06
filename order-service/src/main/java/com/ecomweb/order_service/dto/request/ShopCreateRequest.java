package com.qhuyns.ecomweb.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShopCreateRequest {
    String name;
    String description;
    String fullAddress;
    String province;
    String provinceId;
    String district;
    Integer districtId;
    String ward;
    String wardCode;
    String detailAddress;
}
