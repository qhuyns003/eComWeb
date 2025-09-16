package com.qhuyns.ecomweb.dto.event;

import com.qhuyns.ecomweb.dto.request.ShopCreateRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShopCreated {
    ShopCreateRequest shopCreateRequest;
}
