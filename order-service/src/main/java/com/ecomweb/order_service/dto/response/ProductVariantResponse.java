package com.ecomweb.order_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariantResponse {
    String id;
    Long price;
    Long stock;
    String status;
    List<DetailAttributeResponse> detailAttributes;

}
