package com.qhuyns.ecomweb.dto.request;

import com.qhuyns.ecomweb.dto.response.DetailAttributeResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductAttributeRequest {
    String id;
    String name;
    List<DetailAttributeRequest> detailAttributes;
}
