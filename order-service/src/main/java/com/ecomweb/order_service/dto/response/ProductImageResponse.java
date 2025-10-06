package com.qhuyns.ecomweb.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;


@Setter
@Getter
@NoArgsConstructor // Required nếu k có trường final nào sẽ bị trùng cons với nỏAg
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductImageResponse {
    String id;
    String url;
    Boolean isMain;
}
