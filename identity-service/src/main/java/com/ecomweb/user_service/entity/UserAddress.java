package com.ecomweb.user_service.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "user-address")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAddress {
    @Id
    String id;
    String receiverName;
    String phoneNumber;
    String fullAddress;
    String detailAddress;
    String ward;
    String wardCode;
    String district;
    Integer districtId;
    String province;
    String provinceId;
    // tranh su dung is o ten bien vi de bi loi mapper do buider sinh getter sai isIsDefault -> isDefault, is bi nuot mat
    boolean defaultAddress;
    String userId;
}
