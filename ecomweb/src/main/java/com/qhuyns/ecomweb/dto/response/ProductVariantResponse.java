package com.qhuyns.ecomweb.dto.response;

import com.qhuyns.ecomweb.entity.Cart;
import com.qhuyns.ecomweb.entity.OrderItem;
import com.qhuyns.ecomweb.entity.Product;
import com.qhuyns.ecomweb.entity.ProductAttribute;
import jakarta.persistence.*;
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
