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
public class ReviewStatResponse {
   TreeMap<String,Long> stat;
   Long total;
   Double avrRating;
}
