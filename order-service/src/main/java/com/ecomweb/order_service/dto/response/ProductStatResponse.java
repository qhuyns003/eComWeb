package com.ecomweb.order_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.TreeMap;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductStatResponse {
   Long numberOfOrder;
   Double rating;
}
