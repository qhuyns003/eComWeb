package com.qhuyns.ecomweb.dto.response;

import com.qhuyns.ecomweb.entity.Cart;
import com.qhuyns.ecomweb.entity.OrderItem;
import com.qhuyns.ecomweb.entity.Product;
import com.qhuyns.ecomweb.entity.ProductAttribute;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
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
