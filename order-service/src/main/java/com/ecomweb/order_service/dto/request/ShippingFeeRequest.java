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
public class ShippingFeeRequest {


    private String shopId;
    private Integer serviceTypeId;
    private Integer fromDistrictId;
    private String fromWardCode;
    private Integer toDistrictId;
    private String toWardCode;
    private Integer weight;
    private Integer length;
    private Integer width;
    private Integer height;
    private Integer insuranceValue;
    private String coupon;
    private List<Item> items;

    public static class Item {
        private String name;
        private Integer quantity;
        private Integer length;
        private Integer width;
        private Integer height;
        private Integer weight;

    }
}
