package com.qhuyns.ecomweb.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductAttributeResponse {
    String id;
    String name;
    String status;
    List<DetailAttributeResponse> detailAttributes;
}
